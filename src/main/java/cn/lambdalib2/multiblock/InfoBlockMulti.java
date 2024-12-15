package cn.lambdalib2.multiblock;

import cn.lambdalib2.LambdaLib2;
import net.minecraft.block.Block;
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

    InfoBlockMulti(TileEntity _te, EnumFacing _dir, int sid) {
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

    /**
     * Delegate this method in your TileEntity's updateEntity method.
     */
    public void update() {
        if (te.getWorld().isRemote) {
            if (!loaded) {
                if (syncCD == 0) {
                    LambdaLib2.channel.sendToServer(new MsgBlockMulti.Req(this));
                    syncCD = 10;
                } else {
                    --syncCD;
                }
            }
        } else {
            // Check block consistency every 1s in server.
            if (syncCD == 0) {
                syncCD = 20;
            }
            boolean fail = false;
            Block b = te.getBlockType();
            if (!(b instanceof BlockMulti)) {
                fail = true;
            } else {
                TileEntity ori = ((BlockMulti) b).getOriginTile(te);
                if (ori == null) {
                    fail = true;
                }
            }
            if (fail) { // Kill this block.
                te.getWorld().setBlockToAir(te.getPos());
                te.getWorld().removeTileEntity(te.getPos());
            }
        }
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
