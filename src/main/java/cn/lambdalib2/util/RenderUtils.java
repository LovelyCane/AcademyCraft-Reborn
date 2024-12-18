package cn.lambdalib2.util;

import cn.lambdalib2.render.legacy.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex3d;

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

    public static void glVertexAndUV(double x, double y, double z, double u, double v) {
        glTexCoord2d(u, v);
        glVertex3d(x, y, z);
    }

    public static void glTranslate(Vec3d v) {
        GL11.glTranslated(v.x, v.y, v.z);
    }

    public static void loadTexture(ResourceLocation src) {
        Minecraft.getMinecraft().renderEngine.bindTexture(src);
    }

    public static void drawEquippedItem(double width, ResourceLocation front, ResourceLocation back) {
        drawEquippedItem(width, front, back, 0, 0, 1, 1, false);
    }

    private static void drawEquippedItem(double w, ResourceLocation front, ResourceLocation back, double u1, double v1, double u2, double v2, boolean faceOnly) {
        Tessellator t = Tessellator.instance;
        Vec3d a1 = new Vec3d(0, 0, w), a2 = new Vec3d(1, 0, w), a3 = new Vec3d(1, 1, w), a4 = new Vec3d(0, 1, w), a5 = new Vec3d(0, 0, -w), a6 = new Vec3d(1, 0, -w), a7 = new Vec3d(1, 1, -w), a8 = new Vec3d(0, 1, -w);

        GL11.glPushMatrix();

        RenderUtils.loadTexture(back);
        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, 1.0F);
        addVertex(a1, u2, v2);
        addVertex(a2, u1, v2);
        addVertex(a3, u1, v1);
        addVertex(a4, u2, v1);
        t.draw();

        RenderUtils.loadTexture(front);
        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, -1.0F);
        addVertex(a8, u2, v1);
        addVertex(a7, u1, v1);
        addVertex(a6, u1, v2);
        addVertex(a5, u2, v2);
        t.draw();

        int tileSize = 32;
        float tx = 1.0f / (32 * tileSize);
        float tz = 1.0f / tileSize;

        if (!faceOnly) {
            t.startDrawingQuads();
            t.setNormal(-1.0F, 0.0F, 0.0F);
            for (int var7 = 0; var7 < tileSize; ++var7) {
                float var8 = (float) var7 / tileSize;
                double var9 = u2 - (u2 - u1) * var8 - tx;
                float var10 = 1.0F * var8;
                t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
                t.addVertexWithUV(var10, 0.0D, w, var9, v2);
                t.addVertexWithUV(var10, 1.0D, w, var9, v1);
                t.addVertexWithUV(var10, 1.0D, -w, var9, v1);

                t.addVertexWithUV(var10, 1.0D, w, var9, v1);
                t.addVertexWithUV(var10, 0.0D, w, var9, v2);
                t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
                t.addVertexWithUV(var10, 1.0D, -w, var9, v1);
            }
            t.draw();
        }

        GL11.glPopMatrix();
    }

    private static void addVertex(Vec3d vec, double u, double v) {
        Tessellator t = Tessellator.instance;
        t.addVertexWithUV(vec.x, vec.y, vec.z, u, v);
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