package cn.lambdalib2.multiblock;

import cn.lambdalib2.registry.mc.RegTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * @author WeathFolD
 *
 */
@RegTileEntity
public class TileMulti extends TileEntity implements ITickable {

    InfoBlockMulti info = new InfoBlockMulti(this);
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

    @Override
    public void update() {

    }
}
