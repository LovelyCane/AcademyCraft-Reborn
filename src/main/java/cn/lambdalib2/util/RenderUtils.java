package cn.lambdalib2.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * Utils for legacy GL rendering.
 * Note that you should use modern GL as much as possible and avoid this.
 *
 * @author WeAthFolD
 */
public class RenderUtils {
    private static int textureState = -1;

    //-----------------Quick aliases-----------------------------

    public static void pushTextureState() {
        if (textureState != -1) {
            throw new RuntimeException("RenderUtils:Texture State Overflow");
        }
        textureState = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    public static void popTextureState() {
        if (textureState == -1) {
            throw new RuntimeException("RenderUtils:Texture State Underflow");
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureState);
        textureState = -1;
    }

    public static void glTranslate(Vec3d v) {
        GL11.glTranslated(v.x, v.y, v.z);
    }

    public static void loadTexture(ResourceLocation src) {
        Minecraft.getMinecraft().renderEngine.bindTexture(src);
    }

    public static void addVertexLegacy(Vec3d vertex, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(vertex.x, vertex.y, vertex.z);
    }

    public static void drawBlackout() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(1, 0, 1, 0);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glColor4d(0, 0, 0, 0.7);
        GL11.glTranslated(0, 0, 0);
        HudUtils.colorRect(0, 0, 1, 1);

        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(1, 1, 1, 1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
}