package cn.lambdalib2.multiblock;

import cn.lambdalib2.registry.mc.RegTileEntity;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeathFolD
 *
 */
@RegTileEntity
public class TileMulti extends TileEntity implements IMultiTile, ITickable {

    InfoBlockMulti info = new InfoBlockMulti(this);

    @Override
    public void update() {
        if (info != null)
            info.update();
    }

    @Override
    public InfoBlockMulti getBlockInfo() {
        return info;
    }

    @Override
    public void setBlockInfo(InfoBlockMulti i) {
        info = i;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        info = new InfoBlockMulti(this, nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        info.save(nbt);
        return nbt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        Block block = getBlockType();
        if (block instanceof BlockMulti) {
            return ((BlockMulti) block).getRenderBB(getPos(), info.getDir());
        } else {
            return super.getRenderBoundingBox();
        }
    }

}
