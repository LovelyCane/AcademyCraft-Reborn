package cn.lambdalib2.multiblock;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * The BlockMulti base render, which focuses on placement judging. Concrete
 * render ways belongs to its subclasses.
 *
 * @author WeathFolD
 */

public abstract class RenderBlockMulti<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    public RenderBlockMulti() {
    }

    public abstract void drawAtOrigin(T te);
}
