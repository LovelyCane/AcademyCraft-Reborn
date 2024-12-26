package cn.academy.core.client.ui;

import cn.academy.energy.api.block.IWirelessTile;
import cn.academy.event.energy.UnlinkUserEvent;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkS11n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WirelessNetDelegateJava {
    public static final WirelessNetDelegateJava INSTANCE = new WirelessNetDelegateJava();
    public static final String MSG_FIND_NODES_JAVA = "find_nodes_java";
    public static final String MSG_USER_CONNECT_JAVA = "user_connect_java";
    public static final String MSG_USER_DISCONNECT_JAVA = "unlink_java";

    @StateEventCallback
    private static void init(FMLInitializationEvent ev) {
        NetworkS11n.addDirectInstance(WirelessNetDelegateJava.INSTANCE);
    }

    @NetworkMessage.Listener(channel = MSG_USER_DISCONNECT_JAVA, side = Side.SERVER)
    private <T extends IWirelessTile> void hUserDisconnect(T user, Future<Boolean> fut) {
        UnlinkUserEvent evt = new UnlinkUserEvent(user);
        boolean result = !MinecraftForge.EVENT_BUS.post(evt);

        fut.sendResult(result);
    }
}
