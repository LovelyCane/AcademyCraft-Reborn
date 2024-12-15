package cn.lambdalib2.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;

public class RenderMaterial {

    final ShaderScript shader;

    private Map<Integer, Object> valueMapping = new HashMap<>();
    private HashMap<Integer, Texture2D> textureMapping = new HashMap<>();

    /**
     * Creates a new material from the shader
     * @param shader The shader that this material is based from
     */
    public RenderMaterial(ShaderScript shader) {
        this.shader = shader;
    }

    /**
     * Create a duplicate of given material.
     * @param src LegacyMaterial to be copied
     */
    public RenderMaterial(RenderMaterial src) {
        this(src.shader);
    }

    public int getDrawOrder() {
        return 0;
    }

    public ShaderScript getShader() {
        return shader;
    }

    public void setFloat(String name, float value) {
        setObj(name, value);
    }

    public void setVec2(String name, Vector2f value) {
        setObj(name, value);
    }

    public void setVec3(String name, Vector3f value) {
        setObj(name, value);
    }

    public void setVec4(String name, Vector4f value) {
        setObj(name, value);
    }

    public void setMat4(String name, Matrix4f value) {
        setObj(name, value);
    }

    public void setTexture(String name, Texture2D texture) {
        int location = shader.getUniformLocation(name);
        if (location != -1) {
            textureMapping.put(location, texture);
        } else {
            System.out.println("WARN: Invalid texture name " + name);
        }
    }

    /**
     * Bind current shader program to this material's program,
     *  and apply all the material's properties.
     */
    public void apply() {
        glUseProgram(shader.getProgramID());
        updateUniformsOnCurrentProgram();
    }

    private void setObj(String name, Object value) {
        int location = shader.getUniformLocation(name);
        if (location != -1) {
            valueMapping.put(location, value);
        } else {
            System.out.println("WARN: Invalid uniform object name " + name);
        }
    }

    void updateUniformsOnCurrentProgram() {
        for (ShaderScript.Property property : shader.uniformProperties) {
            int location = property.uniformLocation;
            if (location != -1 && property.type != ShaderScript.PropertyType.Sampler2D) {
                Object value = property.value;
                if (valueMapping.containsKey(location)) {
                    value = valueMapping.get(location);
                }

                if (value instanceof Float) {
                    glUniform1f(location, (Float) value);
                } else if (value instanceof Vector2f) {
                    Vector2f v = (Vector2f) value;
                    glUniform2f(location, v.x, v.y);
                } else if (value instanceof Vector3f) {
                    Vector3f v = (Vector3f) value;
                    glUniform3f(location, v.x, v.y, v.z);
                } else if (value instanceof Vector4f) {
                    Vector4f v = (Vector4f) value;
                    glUniform4f(location, v.x, v.y, v.z, v.w);
                } else if (value instanceof Matrix4f) {
                    FloatBuffer buf = BufferUploadUtils.requestFloatBuffer(16);
                    ((Matrix4f) value).store(buf);
                    buf.flip();
                    glUniformMatrix4(location, false, buf);
                } else {
                    throw new RuntimeException("Invalid uniform type " + value);
                }
            }
        }

        int usedTextureCount = 0;
        Map<Texture2D, Integer> textureToSampler = new HashMap<>();
        Map<Integer, Integer> locationToSampler = new HashMap<>();

        for (Map.Entry<Integer, Texture2D> entry : textureMapping.entrySet()) {
            Texture2D tex = entry.getValue();
            if (!textureToSampler.containsKey(tex)) {
                textureToSampler.put(tex, usedTextureCount++);
            }

            int sampler = textureToSampler.get(tex);
            locationToSampler.put(entry.getKey(), sampler);
        }

        for (Map.Entry<Texture2D, Integer> entry : textureToSampler.entrySet()) {
            glActiveTexture(GL_TEXTURE0 + entry.getValue());
            glBindTexture(GL_TEXTURE_2D, entry.getKey().getTextureID());
        }

        glActiveTexture(GL_TEXTURE0);

        for (Map.Entry<Integer, Integer> entry : locationToSampler.entrySet()) {
            glUniform1i(entry.getKey(), entry.getValue());
        }
    }

    static void loadTexture(ResourceLocation src) {
        Minecraft.getMinecraft().renderEngine.bindTexture(src);
    }

}
