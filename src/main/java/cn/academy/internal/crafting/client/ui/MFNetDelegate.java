package cn.academy.internal.crafting.client.ui;

import cn.academy.internal.tileentity.TileMetalFormer;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import net.minecraftforge.fml.relauncher.Side;

public class MFNetDelegate {
    public static final MFNetDelegate INSTANCE = new MFNetDelegate();
    public static final String MSG_ALTERNATE = "alt";

    @NetworkMessage.Listener(channel = MSG_ALTERNATE, side = {Side.SERVER})
    public void alternate(TileMetalFormer tile, int dir, Future<TileMetalFormer.Mode> fut) {
        tile.cycleMode(dir);
        fut.sendResult(tile.mode);
    }
}
