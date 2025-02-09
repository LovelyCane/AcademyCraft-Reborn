package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class LTNetDelegate {
    public static final String MSG_ADD = "add";
    public static final String MSG_REMOVE = "remove";
    public static final String MSG_PERFORM = "perform";

    public static void send(String channel, Object... args) {
        NetworkMessage.sendToServer(new LTNetDelegate(), channel, args);
    }

    @Listener(channel = MSG_ADD, side = {Side.SERVER})
    private void hAdd(EntityPlayer player, String name, Future<List<Location>> future) {
        LocTeleportData data = LocTeleportData.apply(player);
        data.add(name, player.world.provider.getDimension(), new float[]{(float) player.posX, (float) player.posY, (float) player.posZ});
        future.sendResult(data.locationList);
    }

    @Listener(channel = MSG_REMOVE, side = {Side.SERVER})
    private void hRemove(EntityPlayer player, int id, Future<List<Location>> future) {
        LocTeleportData data = LocTeleportData.apply(player);
        data.remove(id);
        future.sendResult(data.locationList);
    }

    @Listener(channel = MSG_PERFORM, side = {Side.SERVER})
    private void hPerform(EntityPlayer player, Location location) {
        LocationTeleport.perform(player, location);
    }
}