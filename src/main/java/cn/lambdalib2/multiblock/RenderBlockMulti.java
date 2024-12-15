package cn.lambdalib2.multiblock;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

/**
 * The BlockMulti base render, which focuses on placement judging. Concrete
 * render ways belongs to its subclasses.
 * 
 * @author WeathFolD
 */
public abstract class RenderBlockMulti<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    public RenderBlockMulti() {
    }

    @Override
    public void render(T te, double x, double y, double z, float partialTicks,
        int destroyStage, float alpha) {
        if (!(te.getBlockType() instanceof BlockMulti))
            return;

        BlockMulti bm = (BlockMulti) te.getBlockType();
        InfoBlockMulti inf = ((IMultiTile) te).getBlockInfo();

        if (inf == null || !inf.isLoaded() || inf.subID != 0)
            return;
        GL11.glPushMatrix();
        double[] off = bm.getPivotOffset(inf);
        double[] off2 = bm.rotCenters[inf.dir.ordinal()];
        GL11.glTranslated(x + off[0] + off2[0], y + 0 + off2[1], z + off[1] + off2[2]);
        // GL11.glTranslated(x, y, z);
        GL11.glRotated(bm.getRotation(inf), 0, 1, 0);
        drawAtOrigin(te);
        GL11.glPopMatrix();
    }

    public abstract void drawAtOrigin(T te);

}
