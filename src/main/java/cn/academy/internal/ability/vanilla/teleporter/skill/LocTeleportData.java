package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.nbt.NBTS11n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

@RegDataPart(EntityPlayer.class)
public class LocTeleportData extends DataPart<EntityPlayer> {
    @SerializeIncluded
    public List<Location> locationList = new ArrayList<>();

    public LocTeleportData() {
        setNBTStorage();
        setClientNeedSync();
    }

    public static LocTeleportData apply(EntityPlayer player) {
        return EntityData.get(player).getPart(LocTeleportData.class);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        NBTS11n.write(tag, this);
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        NBTS11n.read(tag, this);
    }

    public void add(String name, int dim, float[] pos) {
        checkSide(Side.SERVER);
        locationList.add(new Location(name, dim, pos, locationList.size()));
        sync();
    }

    public void remove(int id) {
        checkSide(Side.SERVER);

        locationList.remove(id);

        for (int i = 0; i < locationList.size(); i++) {
            locationList.get(i).id = i;
        }

        sync();
    }
}