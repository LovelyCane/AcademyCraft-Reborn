package cn.lambdalib2.render;

import cn.lambdalib2.render.ShaderScript.PropertyType;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ResourceUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderScript {

    public enum PropertyType {
        Float(1), Vec2(2), Vec3(3), Vec4(4), Sampler2D(-1), PassData(-1), Mat4(16);

        public final int floatCount;

        PropertyType(int fc) {
            floatCount = fc;
        }
    }

    static class BaseProperty {
        public String name;
        public PropertyType type;
        public Object value;
    }

    static class Property extends BaseProperty {
        int uniformLocation;
    }

    static class InstanceProperty extends BaseProperty{
        int offsetInBuf;
        int location = -1;
    }

    static class VertexAttribute {
        public String name;
        public Mesh.DataType semantic;
        public GLPropertyType type;
        public int index;
    }

    public static ShaderScript load(String content) {
        return ShaderScriptParser.load(content);
    }

    public static ShaderScript load(ResourceLocation location) {
        try {
            return load(IOUtils.toString(ResourceUtils.getResourceStream(location)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final List<Property> uniformProperties = new ArrayList<>();
    public final List<InstanceProperty> instanceProperties = new ArrayList<>();
    public int drawOrder;

    public final RenderStates renderStates = new RenderStates();

    final Map<String, Integer> uniformLocationMapping = new HashMap<>();

    final Map<String, VertexAttribute> vertexLayout = new HashMap<>();

    int floatsPerVertex;
    int floatsPerInstance;

    int glProgramID;

    ShaderScript() {}

    public int getUniformLocation(String uniformName) {
        if (uniformLocationMapping.containsKey(uniformName)) {
            return uniformLocationMapping.get(uniformName);
        }
        return -1;
    }

    public int getProgramID() {
        return glProgramID;
    }

    public void useProgram() {
        glUseProgram(glProgramID);
    }

}

// --- Parsing

final class ShaderScriptParser {

    private static final String
            regexID = "[a-zA-Z][0-9a-zA-Z\\-_]*",
            regexSpace = "(\\p{Space}|[\\r\\n])+",
            regexLeftBrace = "\\{",
            regexRightBrace = "\\}",
            regexLeftParen = "\\(",
            regexRightParen = "\\)",
            regexEq = "=",
            regexComma = ",";

    private static final Token tknID = new Token("ID", regexID);
    private static final Token tknSpace = new Token("SPACE", regexSpace);
    private static final Token tknLeftBrace = new Token("LEFT_BRACE", regexLeftBrace);
    private static final Token tknRightBrace = new Token("RIGHT_BRACE", regexRightBrace);
    private static final Token tknInt = new Token("INT", "[+\\-]?[0-9]+");
    private static final Token tknFloat = new Token("FLOAT", "[+\\-]?[0-9]+\\.[0-9]*([eE][0-9]+)?");
    private static final Token tknSemi = new Token("SEMI", ";");
    private static final Token tknLeftParen = new Token("LEFT_PAREN", regexLeftParen);
    private static final Token tknRightParen = new Token("RIGHT_PAREN", regexRightParen);
    private static final Token tknEq = new Token("EQ", regexEq);
    private static final Token tknComma = new Token("COMMA", regexComma);


    public static ShaderScript load(String content) {
        Lexer mainLexer = new Lexer(content,
                tknID, tknSpace, tknLeftBrace, tknRightBrace);

        ShaderScript script = new ShaderScript();

        String vertexSource = null, fragmentSource = null;

        while (true) {
            MatchedToken t = nextTokenSkipSpaces(mainLexer);
            if (t == null) break;

            if (t.token == tknID) {
                assertToken(mainLexer, nextTokenSkipSpaces(mainLexer), tknLeftBrace);
                int sectionEndPos = parseSection(mainLexer);

                String s = mainLexer.content.substring(mainLexer.currentIndex, sectionEndPos);
                switch (t.content) {
                    case "Properties": {
                        parseProperties(script, s, mainLexer.lineNumber);
                    } break;
                    case "Settings": {
                        parseSettings(script, s, mainLexer.lineNumber);
                    } break;
                    case "Vertex": {
                        vertexSource = s;
                    } break;
                    case "Fragment": {
                        fragmentSource = s;
                    } break;
                    default: throw errorLexer(mainLexer, "Invalid section " + t.content);
                }

                mainLexer.skipTo(sectionEndPos);
                assertToken(mainLexer, nextTokenSkipSpaces(mainLexer), tknRightBrace);

            } else {
                throw errorLexerUnexpected(mainLexer, t, tknID);
            }
        }

        // Check shader integrity
        if (vertexSource == null || fragmentSource == null) {
            throw new RuntimeException("No Vertex or Fragment shader specified");
        }

        // Compile shader
        int programID = glCreateProgram();
        linkShader(programID, GL_VERTEX_SHADER, vertexSource);
        linkShader(programID, GL_FRAGMENT_SHADER, fragmentSource);

        glLinkProgram(programID);
        int result = glGetProgrami(programID, GL_LINK_STATUS);
        if (result == GL_FALSE) {
            String log = glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH));
            throw new RuntimeException("Error when linking shader program: " + log);
        }

        script.glProgramID = programID;

        // Cache info (uniform location)
        for (ShaderScript.Property p : script.uniformProperties) {
            int location = glGetUniformLocation(script.glProgramID, p.name);
            if (location != -1) {
                script.uniformLocationMapping.put(p.name, location);
            }

            p.uniformLocation = location;
        }

        // Precalculate
        int offset = 0;
        for (ShaderScript.InstanceProperty p : script.instanceProperties) {
            Debug.require(p.type.floatCount > 0 && p.type != PropertyType.Mat4, "Unsupported instance type " + p.type + " for " + p.name);
            p.offsetInBuf = offset;
            offset += p.type.floatCount;
        }
        script.floatsPerInstance = offset;

        // Store property layout indexes
        int attrCount = glGetProgrami(script.glProgramID, GL_ACTIVE_ATTRIBUTES);
        ByteBuffer nameBuffer = BufferUtils.createByteBuffer(
            glGetProgrami(script.glProgramID, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH)
        );
        IntBuffer sizeBuffer = BufferUtils.createIntBuffer(1),
                typeBuffer = BufferUtils.createIntBuffer(1),
                lenBuffer = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < attrCount; ++i) {
            nameBuffer.clear();
            sizeBuffer.clear();
            typeBuffer.clear();
            lenBuffer.clear();

            glGetActiveAttrib(script.glProgramID, i, lenBuffer, sizeBuffer, typeBuffer, nameBuffer);
            String name = toString(nameBuffer, lenBuffer.get());
            int index = glGetAttribLocation(script.glProgramID, name);

            ShaderScript.VertexAttribute va = script.vertexLayout.get(name);
            if (va != null) {
                va.index = index;
                va.type = GLPropertyType.fromGLType(typeBuffer.get());
            }

            script.instanceProperties.stream().filter(it -> it.name.equals(name)).findAny().ifPresent(ip -> ip.location = index);
        }

        // Remove invalid attributes
        script.vertexLayout.entrySet().removeIf(it -> it.getValue().index == -1);

        script.floatsPerVertex = script.vertexLayout.values()
                .stream()
                .mapToInt(it -> it.type.components)
                .sum();

        return script;
    }

    private static String toString(ByteBuffer buffer, int len) {
        StringBuilder sb = new StringBuilder();
        while (len-- > 0) {
            sb.append((char) buffer.get());
        }
        return sb.toString();
    }

    private static void linkShader(int programID, int progType, String source) {
        int shaderID = glCreateShader(progType);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        int result = glGetShaderi(shaderID, GL_COMPILE_STATUS);
        if (result == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID, glGetShaderi(shaderID, GL_INFO_LOG_LENGTH));
            throw new RuntimeException("Error when compiling shader: " + log);
        }

        glAttachShader(programID, shaderID);
    }

    private static void parseProperties(ShaderScript shader, String content, int lineNumber) {
        Lexer lexer = new Lexer(content,
                tknID, tknSpace, tknLeftParen, tknRightParen, tknSemi, tknComma,
                tknLeftBrace, tknRightBrace, tknEq, tknFloat, tknInt);

        lexer.setInitPos(lineNumber);

        // section-list := section-list data-section | data-section
        while (true) {
            MatchedToken t = nextTokenSkipSpaces(lexer);
            if (t == null) break;

            if (t.token == tknID) {
                assertToken(lexer, nextTokenSkipSpaces(lexer), tknLeftBrace);

                if (t.content.equals("VertexLayout")) {// layout-list := layout-list layout-statement | layout-list
                    // layout-statement := property-name EQ semantic-name SEMI;
                    while (true) {
                        MatchedToken tName = assertNext(lexer, tknRightBrace, tknID);

                        if (tName.token == tknID) {
                            assertNext(lexer, tknEq);
                            String semantic = assertNext(lexer, tknID).content;
                            assertNext(lexer, tknSemi);

                            Mesh.DataType dataType = semanticToDataType(lexer, semantic);

                            ShaderScript.VertexAttribute va = new ShaderScript.VertexAttribute();
                            va.semantic = dataType;
                            va.index = -1;
                            va.name = tName.content;

                            shader.vertexLayout.put(tName.content, va);
                        } else { // right brace
                            break;
                        }
                    }
                } else {
                    boolean isInstance;
                    switch (t.content) {
                        case "Uniform":
                            isInstance = false;
                            break;
                        case "Instance":
                            isInstance = true;
                            break;
                        default:
                            throw errorLexer(lexer,
                                    String.format("Invalid section name %s, must be Uniform, Instance or VertexLayout",
                                            t.content));
                    }

                    List<ShaderScript.BaseProperty> propertyList = (List) (isInstance ? shader.instanceProperties : shader.uniformProperties);

                    // data-section := section_name { property-list }
                    // property-list := property-list property-statement | property-statement
                    // property-statement := property-name EQ initializer SEMI
                    // initializer := FLOAT | INT | vec2(...) | vec3(...) | vec4(...) | pass_data(ID) | sampler2D

                    while (true) {
                        MatchedToken tName = nextTokenSkipSpaces(lexer);
                        if (tName.token == tknRightBrace) {
                            break;
                        } else if (tName.token == tknID) {
                            String propertyName = tName.content;
                            assertNext(lexer, tknEq);

                            ShaderScript.BaseProperty property = isInstance ? new ShaderScript.InstanceProperty() : new ShaderScript.Property();
                            property.name = propertyName;

                            MatchedToken tInitHead = nextTokenSkipSpaces(lexer);
                            if (tInitHead.token == tknID) {
                                switch (tInitHead.content) {
                                    case "pass_data": {
                                        assertNext(lexer, tknLeftParen);
                                        String dataSource = assertNext(lexer, tknID).content;
                                        assertNext(lexer, tknRightParen);

                                        property.type = PropertyType.PassData;
                                        property.value = dataSource;
                                    }
                                    break;
                                    case "vec2": {
                                        Vector2f vec = new Vector2f();
                                        assertNext(lexer, tknLeftParen);
                                        vec.x = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.y = assertNextNumber(lexer);
                                        assertNext(lexer, tknRightParen);

                                        property.type = PropertyType.Vec2;
                                        property.value = vec;
                                    }
                                    break;
                                    case "vec3": {
                                        Vector3f vec = new Vector3f();
                                        assertNext(lexer, tknLeftParen);
                                        vec.x = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.y = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.z = assertNextNumber(lexer);
                                        assertNext(lexer, tknRightParen);

                                        property.type = PropertyType.Vec3;
                                        property.value = vec;
                                    }
                                    break;
                                    case "vec4": {
                                        Vector4f vec = new Vector4f();
                                        assertNext(lexer, tknLeftParen);
                                        vec.x = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.y = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.z = assertNextNumber(lexer);
                                        assertNext(lexer, tknComma);
                                        vec.w = assertNextNumber(lexer);
                                        assertNext(lexer, tknRightParen);

                                        property.type = PropertyType.Vec4;
                                        property.value = vec;
                                    }
                                    break;
                                    case "sampler2D": {
                                        property.type = PropertyType.Sampler2D;
                                        property.value = null;
                                    }
                                    break;
                                    case "mat4": {
                                        property.type = PropertyType.Mat4;
                                        property.value = new Matrix4f();
                                    }
                                    break;
                                    default: {
                                        throw errorLexer(lexer, "Unsupported property type " + tInitHead.content);
                                    }
                                }
                            } else if (tInitHead.token == tknInt || tInitHead.token == tknFloat) {
                                property.type = PropertyType.Float;
                                property.value = Float.parseFloat(tInitHead.content);
                            } else {
                                throw errorLexerUnexpected(lexer, tInitHead, tknID, tknInt, tknFloat);
                            }

                            propertyList.add(property);

                            assertToken(lexer, nextTokenSkipSpaces(lexer), tknSemi);
                        } else {
                            throw errorLexerUnexpected(lexer, tName, tknRightBrace, tknID);
                        }
                    }
                }

            } else {
                throw errorLexerUnexpected(lexer, t, tknID);
            }
        }
    }

    private static Mesh.DataType semanticToDataType(Lexer lexer, String semantic) {
        switch (semantic) {
            case "POSITION": return Mesh.DataType.Position;
            case "COLOR": return Mesh.DataType.Color;
            case "UV1": return Mesh.DataType.UV1;
            case "UV2": return Mesh.DataType.UV2;
            case "UV3": return Mesh.DataType.UV3;
            case "UV4": return Mesh.DataType.UV4;
            default: throw errorLexer(lexer, "Unknown vertex layout " + semantic);
        }
    }

    private static void parseSettings(ShaderScript shader, String content, int lineNumber) {
        Lexer lexer = new Lexer(content, tknID, tknSpace, tknFloat, tknInt, tknSemi);
        lexer.setInitPos(lineNumber);

        RenderStates renderStates = shader.renderStates;

        while (lexer.hasNext()) {
            MatchedToken t = nextTokenSkipSpaces(lexer);
            if (t == null) break;

            List<MatchedToken> params = new ArrayList<>();
            while (true) {
                MatchedToken pt = nextTokenSkipSpaces(lexer);
                if (pt == null)
                    throw errorEOF();
                if (pt.token == tknSemi) {
                    break;
                } else {
                    params.add(pt);
                }
            }

            // Now on actual parsing of render states
            switch (t.content) {
                case "DepthTest": {
                     renderStates.depthTestMode = RenderStates.TestMode.valueOf(params.get(0).content);
                } break;
                case "DepthMask": {
                    renderStates.depthMask = parseOnOff(lexer, params.get(0).content);
                } break;
                case "AlphaTest": {
                    renderStates.alphaTestMode = RenderStates.TestMode.valueOf(params.get(0).content);
                    renderStates.alphaTestRef = Float.parseFloat(params.get(1).content);
                } break;
                case "Cull": {
                    renderStates.cullMode = RenderStates.CullMode.valueOf(params.get(0).content);
                } break;
                case "ColorMask": {
                    String s = params.get(0).content;
                    if (s.equals("0")) {
                        renderStates.colorMaskA =
                            renderStates.colorMaskR =
                            renderStates.colorMaskG =
                            renderStates.colorMaskB = false;
                    } else {
                        renderStates.colorMaskA = s.contains("A");
                        renderStates.colorMaskR = s.contains("R");
                        renderStates.colorMaskG = s.contains("G");
                        renderStates.colorMaskB = s.contains("B");
                    }
                } break;
                case "DrawOrder": {
                    shader.drawOrder = Integer.parseInt(params.get(0).content);
                } break;
                case "Blend": {
                    renderStates.blending = parseOnOff(lexer, params.get(0).content);
                } break;
                case "BlendFunc": {
                    renderStates.srcBlend = RenderStates.BlendFunc.valueOf(params.get(0).content);
                    renderStates.dstBlend = RenderStates.BlendFunc.valueOf(params.get(1).content);
                } break;
                default: {
                    throw errorLexer(lexer, "Invalid settings property " + t.content);
                }
            }
        }
    }

    private static boolean parseOnOff(Lexer l, String str) {
        str = str.toLowerCase();
        if (str.equals("on"))
            return true;
        if (str.equals("off"))
            return false;
        throw errorLexer(l, "Parameter must be either On or Off");
    }

    private static int parseSection(Lexer lexer) {
        int indentLevel = 1;

        int i;
        for (i = lexer.currentIndex; i < lexer.content.length() && indentLevel != 0; ++i) {
            char ch = lexer.content.charAt(i);
            if (ch == '{') {
                indentLevel += 1;
            } else if (ch == '}') {
                indentLevel -= 1;
            }
        }

        if (indentLevel != 0) throw errorEOF();

        return i - 1;
    }

    private static MatchedToken nextTokenSkipSpaces(Lexer lexer) {
        while (true) {
            MatchedToken m = lexer.next();
            if (m == null || !m.token.name.equals("SPACE"))
                break;
        }

        if (!(lexer.last() == null || !lexer.last().token.name.equals("SPACE")))
            throw new RuntimeException("What the f");
        return lexer.last();
    }

    private static RuntimeException errorLexerUnexpected(Lexer lexer, MatchedToken token, Token... expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Expecting ");
        for (int i = 0; i < expected.length; ++i) {
            sb.append(expected[i].name);
            if (i != expected.length - 1) {
                sb.append('|');
            }
        }

        sb.append(", got ")
            .append(token.token.name)
            .append("(").append(token.content).append(")");
        throw errorLexer(lexer, sb.toString());
    }

    private static RuntimeException errorLexer(Lexer lexer, String msg) {
        return error(String.format("[%d:%d] %s", lexer.lineNumber + 1, lexer.colNumber, msg));
    }

    private static RuntimeException errorEOF() {
        return error("Premature EOF");
    }

    private static MatchedToken assertToken(Lexer l, MatchedToken t, Token... expected) {
        boolean found = false;
        for (Token tk : expected) {
            if (tk == t.token) {
                found = true; break;
            }
        }

        if (!found) {
            throw errorLexerUnexpected(l, t, expected);
        }
        return t;
    }

    private static MatchedToken assertNext(Lexer l, Token ...expected) {
        return assertToken(l, nextTokenSkipSpaces(l), expected);
    }

    private static float assertNumber(Lexer l, MatchedToken t) {
        assertToken(l, t, tknFloat, tknInt);
        return Float.parseFloat(t.content);
    }

    private static float assertNextNumber(Lexer l) {
        return assertNumber(l, nextTokenSkipSpaces(l));
    }

    private static RuntimeException error(String msg) {
        return new RuntimeException(msg);
    }

}

final class Token {
    final String name;
    final Pattern regex;

    Token(String name, String regex) {
        this.name = name;
        this.regex = Pattern.compile("^" + regex);
    }
}

final class MatchedToken {
    final Token token;
    final String content;

    MatchedToken(Token token, String content) {
        this.token = token;
        this.content = content;
    }
}

final class Lexer {

    private final Token[] tokens;

    final String content;

    int currentIndex = 0;
    int lineNumber = 0;
    int colNumber = 0;

    private MatchedToken lastMatched;

    Lexer(String content, Token ...tokens) {
        this.content = content;
        this.tokens = tokens;
    }

    void setInitPos(int lineNumber) {
        this.lineNumber = lineNumber;
        this.colNumber = 0;
    }

    boolean hasNext() {
        return currentIndex != content.length();
    }

    MatchedToken next() {
        if (!hasNext()) {
            lastMatched = null;
            return null;
        }

        for (Token token : tokens) {
            Matcher m = token.regex.matcher(content);
            m.region(currentIndex, content.length());
            if (m.find()) {
                int end = m.end();

                for (int i = currentIndex; i < end; ++i) {
                    char ch = content.charAt(i);
                    if (ch == '\n') {
                        colNumber = 0;
                        lineNumber++;
                    } else {
                        colNumber++;
                    }
                }

                MatchedToken ret = new MatchedToken(token, content.substring(currentIndex, end));
                currentIndex = end;

                lastMatched = ret;
                return ret;
            }
        }

        throw new RuntimeException("Invalid content \"" +
                StringEscapeUtils.escapeJava(content.substring(currentIndex, Math.min(currentIndex + 10, content.length()))) + "\" ...");
    }

    MatchedToken last() {
        return lastMatched;
    }

    void skipTo(int newIndex) {
        for (int i = currentIndex; i < newIndex; ++i) {
            char ch = content.charAt(i);
            if (ch == '\n') {
                colNumber = 0;
                lineNumber++;
            } else {
                colNumber++;
            }
        }
        currentIndex = newIndex;
    }

}