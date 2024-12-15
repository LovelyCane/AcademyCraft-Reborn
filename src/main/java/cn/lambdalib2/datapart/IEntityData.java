package cn.lambdalib2.datapart;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Paindar on 17/10/19.
 */
public interface IEntityData
{
    public void writeNBT(NBTTagCompound tag_);
    public void readNBT(NBTTagCompound tag);
}
