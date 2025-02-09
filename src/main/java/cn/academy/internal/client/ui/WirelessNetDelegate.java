package cn.academy.internal.client.ui;

import cn.academy.internal.energy.api.WirelessHelper;
import cn.academy.internal.energy.api.block.IWirelessNode;
import cn.academy.internal.energy.api.block.IWirelessTile;
import cn.academy.internal.energy.api.block.IWirelessUser;
import cn.academy.internal.energy.impl.NodeConn;
import cn.academy.internal.event.energy.LinkUserEvent;
import cn.academy.internal.event.energy.UnlinkUserEvent;
import cn.academy.internal.tileentity.TileNode;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.SerializeNullable;
import cn.lambdalib2.s11n.SerializeStrategy;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WirelessNetDelegate {
    public static final WirelessNetDelegate INSTANCE = new WirelessNetDelegate();
    public static final String MSG_FIND_NODES = "find_nodes";
    public static final String MSG_USER_CONNECT = "user_connect";
    public static final String MSG_USER_DISCONNECT = "unlink";

    @NetworkMessage.Listener(channel = MSG_USER_CONNECT, side = Side.SERVER)
    private <T extends IWirelessTile> void hUserDisconnect(T user, TileNode target, String password, Future<Boolean> fut) {
        LinkUserEvent evt = new LinkUserEvent(user, target, password);
        boolean result = !MinecraftForge.EVENT_BUS.post(evt);
        fut.sendResult(result);
    }

    @NetworkMessage.Listener(channel = MSG_USER_DISCONNECT, side = Side.SERVER)
    private <T extends IWirelessTile> void hUserDisconnect(T user, Future<Boolean> fut) {
        UnlinkUserEvent evt = new UnlinkUserEvent(user);
        boolean result = !MinecraftForge.EVENT_BUS.post(evt);
        fut.sendResult(result);
    }

    @NetworkMessage.Listener(channel = MSG_FIND_NODES, side = {Side.SERVER})
    private <T extends TileEntity & IWirelessUser> void hFindNodes(T user, Future<UserResult> fut) {
        NodeConn linkedConn = WirelessHelper.getNodeConn(user);
        List<NodeData> nodes = WirelessHelper.getNodesInRange(user.getWorld(), user.getPos()).stream().map(WirelessHelper::getNodeConn).filter(conn -> linkedConn == null || !linkedConn.equals(conn)).map(this::convertToNodeData).collect(Collectors.toList());
        UserResult data = new UserResult();
        if (linkedConn != null) {
            data.linked = convertToNodeData(linkedConn);
        }
        data.avail = nodes;
        fut.sendResult(data);
    }

    private NodeData convertToNodeData(NodeConn conn) {
        TileNode tile = (TileNode) conn.getNode();
        NodeData nodeData = new NodeData();
        nodeData.x = tile.getPos().getX();
        nodeData.y = tile.getPos().getY();
        nodeData.z = tile.getPos().getZ();
        nodeData.encrypted = !tile.getPassword().isEmpty();
        return nodeData;
    }

    @NetworkS11nType
    public static class UserResult {
        @SerializeIncluded
        @SerializeNullable
        public NodeData linked;
        @SerializeIncluded
        public List<NodeData> avail;
    }

    @NetworkS11nType
    @SerializeStrategy(strategy = SerializeStrategy.ExposeStrategy.ALL)
    public static class NodeData {
        public int x;
        public int y;
        public int z;
        public boolean encrypted;

        public TileEntity tile(World world) {
            BlockPos pos = new BlockPos(x, y, z);
            TileEntity tile = world.getTileEntity(pos);
            return (tile instanceof IWirelessNode) ? tile : null;
        }
    }
}
