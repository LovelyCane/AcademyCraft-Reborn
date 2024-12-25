package cn.academy.client.render.block;

import cn.academy.Resources;
import cn.academy.block.tileentity.TileCatEngine;
import cn.lambdalib2.registry.mc.RegTileEntityRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author WeAthFolD
 */

public class RenderCatEngine extends TileEntitySpecialRenderer {
    @RegTileEntityRender(TileCatEngine.class)
    private static final RenderCatEngine instance = new RenderCatEngine();

    static final ResourceLocation TEXTURE = Resources.getTexture("blocks/cat_engine");

    @Override
    public void render(TileEntity tile, double x, double y, double z, float pt, int destroyStage, float alpha) {
        TileCatEngine engine = (TileCatEngine) tile;

        x += 0.5;
        z += 0.5;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.translate(x, y + 0.03 * 1, z);

        double yaw = Math.atan2(x, z) * 180 / Math.PI;
        GlStateManager.rotate((float) (yaw + 180), 0, 1, 0);

        GlStateManager.translate(0, .5, 0);
        GlStateManager.rotate((float) engine.rotation, 1, 0, 0);
        GlStateManager.translate(-0.5, -0.5, 0);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 0, 0).tex(0, 0).endVertex();
        buffer.pos(1, 0, 0).tex(1, 0).endVertex();
        buffer.pos(1, 1, 0).tex(1, 1).endVertex();
        buffer.pos(0, 1, 0).tex(0, 1).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}