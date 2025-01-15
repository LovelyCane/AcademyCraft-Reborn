package cn.lambdalib2.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Class that stores and handles per-block orientation&sub block ID info. You
 * should delegate the save&store methods via your TileEntity that implements
 * IMultiTile.
 * 
 * @author WeathFolD
 */
public class InfoBlockMulti {

    final TileEntity te;

    EnumFacing dir = EnumFacing.NORTH;
    int subID;

    private boolean loaded; // Client-Only flag. Indicate if it was synced.
    int syncCD; // Ticks until sending next sync request.

    public InfoBlockMulti(TileEntity _te, EnumFacing _dir, int sid) {
        te = _te;
        dir = _dir;
        subID = sid;
    }

    public InfoBlockMulti(TileEntity _te) {
        te = _te;
    }

    /**
     * Use this Ctor to restore your Info in TileEntity's readNBT method.
     */
    public InfoBlockMulti(TileEntity _te, NBTTagCompound tag) {
        te = _te;
        load(tag);
    }

    public boolean isLoaded() {
        return te.getWorld().isRemote ? loaded : true;
    }

    public int getSubID() {
        return subID;
    }

    public EnumFacing getDir() {
        return dir;
    }

    public void setLoaded() {
        loaded = true;
    }

    public void save(NBTTagCompound tag) {
        tag.setByte("dir", (byte) dir.ordinal());
        tag.setInteger("sub", subID);
    }

    public void load(NBTTagCompound tag) {
        if(tag.hasKey("dir")) {
            dir = EnumFacing.values()[tag.getByte("dir")];
            loaded=true;
        }
        if(tag.hasKey("sub")) {
            subID = tag.getInteger("sub");
            loaded = true;
        }
    }
}
