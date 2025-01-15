/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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

    private static void addVertexWithUV(BufferBuilder bb, double x, double y, double z, double u, double v) {
        bb.pos(x, y, z).tex(u, v).endVertex();
    }
}
