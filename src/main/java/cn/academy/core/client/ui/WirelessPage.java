package cn.academy.core.client.ui;

import cn.academy.block.tileentity.TileMatrix;
import cn.academy.block.tileentity.TileNode;
import cn.academy.energy.api.WirelessHelper;
import cn.academy.energy.api.block.IWirelessMatrix;
import cn.academy.energy.api.block.IWirelessNode;
import cn.academy.energy.api.block.IWirelessTile;
import cn.academy.energy.api.block.IWirelessUser;
import cn.academy.energy.impl.NodeConn;
import cn.academy.energy.impl.WirelessNet;
import cn.academy.event.energy.LinkNodeEvent;
import cn.academy.event.energy.LinkUserEvent;
import cn.academy.event.energy.UnlinkNodeEvent;
import cn.academy.event.energy.UnlinkUserEvent;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.ElementList;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.SerializeNullable;
import cn.lambdalib2.s11n.SerializeStrategy;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WirelessPage {
    private static final Widget template = CGUIDocument.read(new ResourceLocation("academy:guis/rework/" + "page_wireless" + ".xml")).getWidget("main");
    public static final String MSG_FIND_NODES = "find_nodes";
    public static final String MSG_USER_CONNECT = "user_connect";
    public static final String MSG_USER_DISCONNECT = "unlink";
    public static final String MSG_NODE_CONNECT = "node_connect";
    public static final String MSG_NODE_DISCONNECT = "node_disconnect";
    public static final String MSG_FIND_NETWORKS = "find_networks";

    public static Page userPage(TileEntity user) {
        Page ret = apply();
        World world = user.getWorld();
        return ret;
    }

    public static Page apply() {
        Widget widget = template.copy();
        Widget wirelessPanel = widget.getWidget("panel_wireless");
        Widget wlist = wirelessPanel.getWidget("zone_elementlist");

        ElementList elist = wlist.getComponent(ElementList.class);
        wirelessPanel.getWidget("btn_arrowup").listen(LeftClickEvent.class, (w, event) -> elist.progressLast());
        wirelessPanel.getWidget("btn_arrowdown").listen(LeftClickEvent.class, (w, event) -> elist.progressNext());

        return new Page("wireless", widget);
    }

    private static void send(String msg, Object... pars) {
        NetworkMessage.sendToServer(WirelessNetDelegate.class, msg, pars);
    }
}

interface Target {
    String name();
}

interface LinkedTarget extends Target {
    void disconnect();
}

class TileUser extends TileEntity implements IWirelessUser {}
class TileBase extends TileEntity implements IWirelessTile {}

@NetworkS11nType
@SerializeStrategy(strategy = SerializeStrategy.ExposeStrategy.ALL)
class NodeData {
    private int x, y, z;
    private boolean encrypted;

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    public TileEntity tile(World world) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if (tileEntity instanceof IWirelessNode) {
            return tileEntity;
        }
        return null;
    }
}

@NetworkS11nType
@SerializeStrategy(strategy = SerializeStrategy.ExposeStrategy.ALL)
class MatrixData {
    private int x, y, z;
    private String ssid;
    private boolean encrypted;

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }
    public String getSsid() { return ssid; }
    public void setSsid(String ssid) { this.ssid = ssid; }
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    public Optional<TileEntity> tile(World world) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return tile instanceof IWirelessMatrix ? Optional.of(tile) : Optional.empty();
    }
}

@NetworkS11nType
class UserResult {
    @SerializeIncluded
    @SerializeNullable
    private NodeData linked;
    @SerializeIncluded
    private NodeData[] avail;

    public NodeData getLinked() { return linked; }
    public void setLinked(NodeData linked) { this.linked = linked; }
    public NodeData[] getAvail() { return avail; }
    public void setAvail(NodeData[] avail) { this.avail = avail; }
}

@NetworkS11nType
class NodeResult {
    @SerializeIncluded
    @SerializeNullable
    private MatrixData linked;
    @SerializeIncluded
    private MatrixData[] avail;

    public MatrixData getLinked() { return linked; }
    public void setLinked(MatrixData linked) { this.linked = linked; }
    public MatrixData[] getAvail() { return avail; }
    public void setAvail(MatrixData[] avail) { this.avail = avail; }
}

class WirelessNetDelegate {
    @StateEventCallback
    public static void __init(FMLInitializationEvent ev) {
        NetworkS11n.addDirectInstance(new WirelessNetDelegate());
    }

    @Listener(channel = WirelessPage.MSG_FIND_NODES, side = {Side.SERVER})
    private void hFindNodes(TileUser user, Future<UserResult> fut) {
        Function<NodeConn, NodeData> cvt = conn -> {
            TileNode tile = (TileNode) conn.getNode();
            NodeData ret = new NodeData();
            ret.setX(tile.getPos().getX());
            ret.setY(tile.getPos().getY());
            ret.setZ(tile.getPos().getZ());
            ret.setEncrypted(!tile.getPassword().isEmpty());
            return ret;
        };

        Optional<NodeConn> linked = Optional.ofNullable(WirelessHelper.getNodeConn(user));
        List<NodeConn> nodes = WirelessHelper.getNodesInRange(user.getWorld(), user.getPos())
                .stream()
                .map(WirelessHelper::getNodeConn)
                .filter(conn -> linked.map(link -> !link.equals(conn)).orElse(true))
                .collect(Collectors.toList());

        UserResult data = new UserResult();
        data.setLinked(linked.map(cvt).orElse(null));
        data.setAvail(nodes.stream().map(cvt).toArray(NodeData[]::new));
        fut.sendResult(data);
    }

    @Listener(channel = WirelessPage.MSG_FIND_NETWORKS, side = {Side.SERVER})
    private void hFindNetworks(TileNode node, Future<NodeResult> fut) {
        Optional<WirelessNet> linked = Optional.ofNullable(WirelessHelper.getWirelessNet(node));
        Function<WirelessNet, MatrixData> cvt = net -> {
            TileEntity mat = (TileEntity) net.getMatrix();
            MatrixData ret = new MatrixData();
            ret.setX(mat.getPos().getX());
            ret.setY(mat.getPos().getY());
            ret.setZ(mat.getPos().getZ());
            ret.setSsid(net.getSSID());
            ret.setEncrypted(!net.getPassword().isEmpty());
            return ret;
        };

        List<WirelessNet> networks = WirelessHelper.getNetInRange(node.getWorld(), node.getPos().getX(), node.getPos().getY(), node.getPos().getZ(), node.getRange(), 20)
                .stream()
                .filter(conn -> linked.map(link -> !link.equals(conn)).orElse(true))
                .collect(Collectors.toList());

        NodeResult data = new NodeResult();
        data.setLinked(linked.map(cvt).orElse(null));
        data.setAvail(networks.stream().map(cvt).toArray(MatrixData[]::new));
        fut.sendResult(data);
    }

    @Listener(channel = WirelessPage.MSG_USER_CONNECT, side = {Side.SERVER})
    private void hUserConnect(TileUser user, TileNode target, String password, Future<Boolean> fut) {
        LinkUserEvent evt = new LinkUserEvent(user, target, password);
        boolean result = !MinecraftForge.EVENT_BUS.post(evt);
        fut.sendResult(result);
    }

    @Listener(channel = WirelessPage.MSG_USER_DISCONNECT, side = {Side.SERVER})
    private void hUserDisconnect(TileBase user, Future<Boolean> fut) {
        UnlinkUserEvent evt = new UnlinkUserEvent(user);
        boolean result = !MinecraftForge.EVENT_BUS.post(evt);
        fut.sendResult(result);
    }

    @Listener(channel = WirelessPage.MSG_NODE_CONNECT, side = {Side.SERVER})
    private void hNodeConnect(TileNode node, TileMatrix mat, String pwd, Future<Boolean> fut) {
        boolean result = !MinecraftForge.EVENT_BUS.post(new LinkNodeEvent(node, mat, pwd));
        fut.sendResult(result);
    }

    @Listener(channel = WirelessPage.MSG_NODE_DISCONNECT, side = {Side.SERVER})
    private void hNodeDisconnect(TileNode node, Future<Boolean> fut) {
        MinecraftForge.EVENT_BUS.post(new UnlinkNodeEvent(node));
        fut.sendResult(true);
    }
}
