package cn.academy.block.tileentity;

import cn.academy.client.render.block.RenderWindGenPillar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;

/**
 * @author WeAthFolD
 */
@RegTileEntity
@RegTileEntity.HasRender
public class TileWindGenPillar extends TileEntity {
    
    @SideOnly(Side.CLIENT)
    @RegTileEntity.Render
    public static RenderWindGenPillar renderer;
    
}