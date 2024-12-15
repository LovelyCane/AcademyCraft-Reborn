package cn.lambdalib2.render.font;

import cn.lambdalib2.util.Colors;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Paindar on 2017/10/15.
 */
public class TrueTypeFont implements IFont {

    static class CachedChar{
        int ch;
        int width;
        int index;
        float u;
        float v;

        CachedChar(int ch,int w,int i,float u,float v){
            this.ch=ch;
            this.width=w;
            this.index=i;
            this.u=u;
            this.v=v;
        }
    }

    static class Vertex {
        float x, y, z, u, v;

        public Vertex(float x, float y, float z, float u, float v) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
        }
    }

    public static TrueTypeFont defaultFont = withFallback(Font.PLAIN, 32,
            "Microsoft YaHei", "Adobe Heiti Std R", "STHeiti",
            "SimHei", "微软雅黑", "黑体",
            "Consolas", "Monospace", "Arial");

    public static TrueTypeFont withFallback(int style, int size, String... fallbackNames){
        Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        List<Font> used=new ArrayList<>();
        for (String c : fallbackNames) {
            for(Font ex:allfonts){
                if(ex.getName().equalsIgnoreCase(c)) {
                    used.add(ex);
                    break;
                }
            }
        }
        return (used.isEmpty()) ? new TrueTypeFont(new Font(null, style, size)) : new TrueTypeFont(new Font(used.get(0).getName(), style, size));
    }

    private static Color BACKGRND_COLOR = new Color(255, 255, 255, 0);
    private final int TEXTURE_SZ_LIMIT = Math.min(2048, GL11.glGetInteger(GL_MAX_TEXTURE_SIZE));

    private final int charSize;
    private final float maxPerCol;
    private final float maxStep;
    private List<Integer> generated = new ArrayList<>();
    private BitSet dirty = new BitSet();
    private Map<Integer, CachedChar> lookup = new HashMap<>();
    private int step = 0;
    private float texStep;

    @SuppressWarnings("unchecked")
    private List<Vertex>[] batchInfoCache = new List[8];

    public final Font font;

    public TrueTypeFont(Font font){
        this.font=font;
        charSize=(int)(font.getSize() * 1.4);
        maxPerCol = MathHelper.floor(1.0*TEXTURE_SZ_LIMIT / charSize);
        maxStep = maxPerCol * maxPerCol;
        texStep = 1.0f / maxPerCol;
        newTexture();

        for (int i = 0; i < batchInfoCache.length; ++i) {
            batchInfoCache[i] = new ArrayList<>();
        }
    }

    private int currentTexture(){
        return generated.get(generated.size() - 1);
    }

    private Font resolve(int codePoint) {
        return font;
    }

    /**
     * Draws the string at the given position with given font option in one line. <br>
     * <p>
     * The string is assumed to not include line-seperate characters. (\n or \r). Violating this yields undefined
     * behaviour.
     */
    @Override
    public void draw(String str, float px, float y, FontOption option) {
        int lastTextureBinding = glGetInteger(GL_TEXTURE_BINDING_2D);

        float len = getTextWidth(str, option); // Which will call updateCache()
        for(int i=0;i<dirty.size();i++){
            if(dirty.get(i)) {
                glBindTexture(GL_TEXTURE_2D, generated.get(i));
                GL30.glGenerateMipmap(GL_TEXTURE_2D);
            }
        }
        dirty.clear();

        float x = px;
        float sz = option.fontSize;
        float scale = option.fontSize / charSize;

        x = px - len * option.align.lenOffset;

        boolean preEnabled = glIsEnabled(GL_ALPHA_TEST);
        int preFunc = glGetInteger(GL_ALPHA_TEST_FUNC);
        float preRef = glGetFloat(GL_ALPHA_TEST_REF);
        glDisable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GEQUAL, 0.1f);
        glEnable(GL_TEXTURE_2D);

        for (int i = 0; i < batchInfoCache.length; ++i) {
            batchInfoCache[i].clear();
        }

        for(int i:codePoints(str)){
            CachedChar info = lookup.get(i);
            float u = info.u;
            float v = info.v;
            List<Vertex> list = batchInfoCache[info.index];
            list.add(new Vertex(x,      y,      0, u,           v          ));
            list.add(new Vertex(x,      y + sz, 0, u,           v + texStep));
            list.add(new Vertex(x + sz, y + sz, 0, u + texStep, v + texStep));
            list.add(new Vertex(x + sz, y,      0, u + texStep, v          ));

            x += info.width * scale;
        }

        for (int i = 0; i < batchInfoCache.length; ++i) {
            List<Vertex> list = batchInfoCache[i];
            if (!list.isEmpty()) {
                int texture = generated.get(i);
                Colors.bindToGL(option.color);
                glBindTexture(GL_TEXTURE_2D, texture);
                glBegin(GL_QUADS);
                for (Vertex v : list) {
                    glTexCoord2d(v.u, v.v);
                    glVertex3d(v.x, v.y, v.z);
                }
                glEnd();
            }
        }

        if (preEnabled) {
            glEnable(GL_ALPHA_TEST);
        }
        glAlphaFunc(preFunc, preRef);

        glBindTexture(GL_TEXTURE_2D, lastTextureBinding);
    }

    private List<Integer> codePoints(String str){
        List<Integer> list=new ArrayList<>();
        for(int i=0;i<str.length();i++){
            list.add(str.codePointAt(i));
        }
        return list;
    }

    /**
     * Get the width of given character when drawn with given FontOption.
     */
    @Override
    public float getCharWidth(int chr, FontOption option)
    {
        if(!lookup.containsKey(chr)) {
            writeImage(chr);
        }
        return (lookup.get(chr)).width * option.fontSize / charSize;
    }

    /**
     * Get the text width that will be drawn if calls the {@link IFont#draw}.
     */
    @Override
    public float getTextWidth(String str, FontOption option)
    {
        updateCache(str);
        float sum=0;
        for(int i:codePoints(str)){
            sum+= (lookup.get(i)).width;
        }
        return sum * option.fontSize / charSize;
    }

    private void newTexture(){
        int texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, TEXTURE_SZ_LIMIT, TEXTURE_SZ_LIMIT, 0, GL_RGBA, GL_FLOAT,
                (ByteBuffer)null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.65f);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        glBindTexture(GL_TEXTURE_2D, 0);

        generated.add(texture);
        step = 0;
    }

    // Update the cached images to contain the given new characters.
    private void updateCache(String str) {
        Set<Integer> newchars = new HashSet<>();
        for(int i : codePoints(str)) {
            if(!lookup.containsKey(i))
                newchars.add(i);
        }
        newchars.forEach(this::writeImage);
    }

    // Draw the image into the cached textures at current step position and increment the step by 1.
    private void writeImage(int ch){
        // Create an image holding the character
        BufferedImage image = new BufferedImage(charSize, charSize, BufferedImage.TYPE_INT_ARGB);
        int curtex = currentTexture();

        Graphics2D graphics = image.createGraphics();
        Font drawFont = resolve(ch);

        graphics.setFont(drawFont);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics metrics = graphics.getFontMetrics();
        int width = metrics.charWidth((char) ch);
        // Draw to the image
        graphics.setBackground(BACKGRND_COLOR);
        graphics.clearRect(0, 0, charSize, charSize);
        graphics.setColor(Color.WHITE);

        graphics.drawString(new StringBuilder(2).appendCodePoint(ch).toString(), 3, 1 + metrics.getAscent());

        ByteBuffer byteBuffer;
        DataBuffer db = image.getData().getDataBuffer();
        Byte bpp = (byte) image.getColorModel().getPixelSize();
        if (db instanceof DataBufferInt) {
            int[] rawData = ((DataBufferInt) image.getData().getDataBuffer()).getData();
            byte[] bytes = new byte[rawData.length * 4];
            for(int i=0; i < rawData.length; i++) {
                int val = rawData[i];
                int newIndex = i*4;

                byte r = (byte) ((val >>> 24) & 0xFF);
                byte g = (byte) ((val >>> 16) & 0xFF);
                byte b = (byte) ((val >>> 8) & 0xFF);
                byte l = (byte) ((int) (r + g + b) / 3);
                bytes[newIndex] = bytes[newIndex + 1] = bytes[newIndex + 2] = -1;
                bytes[newIndex+3]= (byte) ((val & 0xFF) * l / 255);
            }

            byteBuffer = ByteBuffer.allocateDirect(
                    charSize*charSize*(bpp/8))
                    .order(ByteOrder.nativeOrder())
                    .put(bytes);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(
                    charSize*charSize*(bpp/8))
                    .order(ByteOrder.nativeOrder())
                    .put(((DataBufferByte)image.getData().getDataBuffer()).getData());
        }
        byteBuffer.flip();

        // write the image to texture
        int rasterX = (int)(step % maxPerCol) * charSize;
        int rasterY = (int)(step / maxPerCol) * charSize;

        glBindTexture(GL_TEXTURE_2D, curtex);
        glTexSubImage2D(GL_TEXTURE_2D, 0, rasterX, rasterY, charSize, charSize, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

        lookup.put(ch, new CachedChar(ch, width, generated.size() - 1, 1.0f*rasterX / TEXTURE_SZ_LIMIT,
                1.0f*rasterY / TEXTURE_SZ_LIMIT));

        step += 1;
        if (step == maxStep) {
            step = 0;
            newTexture();
        }

        dirty.set(generated.size() - 1);

        graphics.dispose();
    }
}
