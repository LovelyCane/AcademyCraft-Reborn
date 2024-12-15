package cn.lambdalib2.render;

import cn.lambdalib2.util.ResourceUtils;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture2D {

    public static Texture2D load(ResourceLocation loc, TextureImportSettings settings) {
        return loadFromStream(ResourceUtils.getResourceStream(loc), settings);
    }

    public static Texture2D loadFromResource(String path, TextureImportSettings settings) {
        try {
            return loadFromStream(Texture2D.class.getResource(path).openStream(), settings);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static int fixColor(int x) {
        int r = (x      ) & 0xFF;
        int g = (x >> 8 ) & 0xFF;
        int b = (x >> 16) & 0xFF;
        int a = (x >> 24) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    private static Texture2D loadFromStream(InputStream imageStream, TextureImportSettings settings) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(imageStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        int[] arr = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        // TODO figure out why this is needed and if it is platform-dependent
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = fixColor(arr[i]);
        }

        IntBuffer buffer = BufferUploadUtils.requestIntBuffer(image.getWidth() * image.getHeight());

        buffer.put(arr);
        buffer.flip();

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        switch (settings.filterMode) {
            case Point:
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                break;
            case Blinear:
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                break;
            case Trilinear:
                glGenerateMipmap(GL_TEXTURE_2D);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                break;
        }

        switch (settings.wrapMode) {
            case Clamp:
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
                break;
            case Repeat:
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                break;
        }

        glBindTexture(GL_TEXTURE_2D, 0);

        return new Texture2D(textureID);
    }

    private final int textureID;

    private Texture2D(int textureID) {
        this.textureID = textureID;
    }

    public void destroy() {
        glDeleteTextures(textureID);
    }

    public int getTextureID() {
        return textureID;
    }

}
