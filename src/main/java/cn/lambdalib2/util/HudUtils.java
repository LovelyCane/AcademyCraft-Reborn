/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author WeAthFolD
 *
 */
public class HudUtils {
    public static double zLevel = 0;
    
    static double stack = Double.NEGATIVE_INFINITY;
    
    public static void pushZLevel() {
        if(stack != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("Stack overflow");
        stack = zLevel;
    }
    
    public static void popZLevel() {
        if(stack == Double.NEGATIVE_INFINITY)
            throw new RuntimeException("Stack underflow");
        zLevel = stack;
        stack = Double.NEGATIVE_INFINITY;
    }

    public static void loadTexture(ResourceLocation res) {
        Minecraft.getMinecraft().renderEngine.bindTexture(res);
    }
    
    public static void rect(double width, double height) {
        rect(0, 0, width, height);
    }
    
    public static void rect(double x, double y, double width, double height) {
        rawRect(x, y, 0, 0, width, height, 1, 1);
    }
    
    public static void rect(double x, double y, double u, double v, double width, double height) {
        rect(x, y, u, v, width, height, width, height);
    }
    
    public static void rect(double x, double y, double u, double v, double width, double height, double texWidth, double texHeight) {
        int twidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH),
            theight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        double f = 1.0 / twidth, f1 = 1.0 / theight;
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb= t.getBuffer();
        glEnable(GL_TEXTURE_2D);
        bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        addVertexWithUV(bb, x + 0,      y + height, zLevel, (u + 0) * f,          (v + texHeight) * f1);
        addVertexWithUV(bb, x + width, y + height, zLevel, (u + texWidth) * f, (v + texHeight) * f1);
        addVertexWithUV(bb, x + width, y + 0,      zLevel, (u + texWidth) * f, (v + 0) * f1);
        addVertexWithUV(bb, x + 0,      y + 0,      zLevel, (u + 0) * f,          (v + 0) * f1);
        t.draw();
    }
    
    public static void rawRect(double x, double y, double u, double v, double width, double height, double texWidth, double texHeight) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb = t.getBuffer();
        bb.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        addVertexWithUV(bb, x + 0,      y + height, zLevel, (u + 0),          (v + texHeight));
        addVertexWithUV(bb, x + width, y + height, zLevel, (u + texWidth), (v + texHeight));
        addVertexWithUV(bb, x + width, y + 0,         zLevel, (u + texWidth), (v + 0));
        addVertexWithUV(bb, x + 0,      y + 0,      zLevel, (u + 0),          (v + 0));
        t.draw();
    }
    
    public static void colorRect(double x, double y, double width, double height) {
        boolean prev = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb = t.getBuffer();
        bb.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x + 0, y + height, zLevel).endVertex();
        bb.pos(x + width, y + height, zLevel).endVertex();
        bb.pos(x + width, y + 0, zLevel).endVertex();
        bb.pos(x + 0, y + 0, zLevel).endVertex();
        t.draw();

        if(prev) GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static void drawRectOutline(double x, double y, double w, double h, float lineWidth) {
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glBegin(GL_LINE_LOOP);
        double lw = lineWidth * 0.2;
        x -= lw;
        y -= lw;
        w += 2 * lw;
        h += 2 * lw;
        glVertex3d(x, y, zLevel);
        glVertex3d(x, y + h, zLevel);
        glVertex3d(x + w, y + h, zLevel);
        glVertex3d(x + w, y, zLevel);
        glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static void drawHoveringText(List par1List, int x, int y, FontRenderer font, int width, int height) {
        if (!par1List.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;

            for (Object o : par1List) {
                String s = (String) o;
                int l = font.getStringWidth(s);

                if (l > k) {
                    k = l;
                }
            }

            int i1 = x + 12;
            int j1 = y - 12;
            int k1 = 8;

            if (par1List.size() > 1) {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > width) {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > height) {
                j1 = height - k1 - 6;
            }

            int l1 = -267386864;
            drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2) {
                String s1 = (String)par1List.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0) {
                    j1 += 2;
                }

                j1 += 10;
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
    
    public static void drawGradientRect(int x0, int y0, int x1, int y1, int color1, int color2) {
        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        glBegin(GL_QUADS);

        glColor4f(f1, f2, f3, f);
        glVertex3d(x1, y0, -90D);
        glVertex3d(x0, y0, -90D);

        glColor4f(f5, f6, f7, f4);
        glVertex3d(x0, y1, -90D);
        glVertex3d(x1, y1, -90D);

        glEnd();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void addVertexWithUV(BufferBuilder bb, double x, double y, double z, double u, double v) {
        bb.pos(x, y, z).tex(u, v).endVertex();
    }
}
