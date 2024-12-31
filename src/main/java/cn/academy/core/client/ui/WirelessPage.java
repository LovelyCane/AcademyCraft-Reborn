package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.academy.block.tileentity.TileNode;
import cn.academy.energy.api.WirelessHelper;
import cn.academy.energy.api.block.IWirelessNode;
import cn.academy.energy.api.block.IWirelessTile;
import cn.academy.energy.api.block.IWirelessUser;
import cn.academy.energy.impl.NodeConn;
import cn.academy.event.energy.LinkUserEvent;
import cn.academy.event.energy.UnlinkUserEvent;
import cn.academy.util.LocalHelper;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.GainFocusEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.event.LostFocusEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.SerializeNullable;
import cn.lambdalib2.s11n.SerializeStrategy;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import cn.lambdalib2.util.Colors;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cn.academy.core.client.ui.WirelessNetDelegate.*;

public class WirelessPage {
    private static final Widget wirelessPageTemplate = CGUIDocument.read(Resources.getGui("rework/page_wireless")).getWidget("main");
    private static final ResourceLocation connectedIcon = Resources.getTexture("guis/icons/icon_connected");
    private static final ResourceLocation unconnectedIcon = Resources.getTexture("guis/icons/icon_unconnected");
    private static final LocalHelper local = ContainerUI.local.subPath("pg_wireless");

    public interface Target {
        String name();
    }

    public interface AvailTarget extends Target {
        void connect(String pass);

        boolean encrypted();
    }

    public interface LinkedTarget extends Target {
        void disconnect();
    }

    public static class LinkedInfo extends Component {
        private LinkedTarget target;

        public LinkedInfo(LinkedTarget target) {
            super("LinkedInfo");
            this.target = target;
        }

        public LinkedTarget getTarget() {
            return target;
        }

        public void setTarget(LinkedTarget target) {
            this.target = target;
        }
    }

    private static void rebuildPage(Widget window, LinkedTarget linked, List<AvailTarget> avail) {
        Widget wlist = window.getWidget("panel_wireless/zone_elementlist");
        wlist.removeComponent(ElementList.class);
        ElementList elist = new ElementList();
        wlist.getWidget("element").transform.doesDraw = false;
        Widget elemTemplate = wlist.getWidget("element").copy();
        Widget connectElem = window.getWidget("panel_wireless/elem_connected");
        if (linked.name() != null) {
            connectElem.getWidget("icon_connect").getComponent(DrawTexture.class).texture = connectedIcon;
            connectElem.getWidget("icon_connect").getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(1.0f));
            connectElem.getWidget("icon_connect").getComponent(Tint.class).enabled = true;
            connectElem.getWidget("icon_logo").getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(1.0f));
            connectElem.getWidget("text_name").getComponent(TextBox.class).setContent(linked.name());
            connectElem.getWidget("icon_connect").getComponent(LinkedInfo.class).setTarget(linked);
        } else {
            connectElem.getWidget("icon_connect").getComponent(DrawTexture.class).texture = unconnectedIcon;
            connectElem.getWidget("icon_connect").getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(0.6f));
            connectElem.getWidget("icon_connect").getComponent(Tint.class).enabled = false;
            connectElem.getWidget("text_name").getComponent(TextBox.class).setContent(local.get("not_connected"));
        }

        for (AvailTarget target : avail) {
            Widget instance = elemTemplate.copy();
            Widget passBox = instance.getWidget("input_pass");
            Widget iconKey = instance.getWidget("icon_key");
            instance.getWidget("text_name").getComponent(TextBox.class).setContent(target.name());
            if (target.encrypted()) {
                passBox.getComponent(TextBox.class).listen(TextBox.ConfirmInputEvent.class, (widget, confirmInputEvent) -> confirm(passBox, target));
                passBox.listen(GainFocusEvent.class, () -> iconKey.getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(1.0f)));
                passBox.listen(LostFocusEvent.class, () -> iconKey.getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(0.6f)));
            } else {
                passBox.transform.doesDraw = false;
                iconKey.transform.doesDraw = false;
            }
            instance.getWidget("icon_connect").listen(LeftClickEvent.class, () -> confirm(passBox, target));
            elist.addWidget(instance);
        }
        wlist.addComponent(elist);
    }

    public static Page userPage(TileEntity user) {
        Page ret = WirelessPage.apply();
        World world = user.getWorld();
        rebuild(user, world, ret);
        return ret;
    }

    public static Future<Boolean> newFuture(TileEntity user, World world, Page ret) {
        return Future.create(aBoolean -> rebuild(user, world, ret));
    }

    private static void rebuild(TileEntity user, World world, Page ret) {
        sendJava(MSG_FIND_NODES, user, Future.create((Consumer<WirelessNetDelegate.UserResult>) result -> {
            LinkedTarget linkedTarget = new LinkedTarget() {
                @Override
                public void disconnect() {
                    sendJava(MSG_USER_DISCONNECT, user, newFuture(user, world, ret));
                }

                @Override
                public String name() {
                    IWirelessNode node = result.linked == null ? null : (IWirelessNode) result.linked.tile(world);
                    return node == null ? null : node.getNodeName();
                }
            };

            List<AvailTarget> availTargets = new ArrayList<>();
            for (WirelessNetDelegate.NodeData data : result.avail) {
                IWirelessNode node = (IWirelessNode) data.tile(world);
                AvailTarget availTarget = new AvailTarget() {
                    @Override
                    public String name() {
                        return node.getNodeName();
                    }

                    @Override
                    public void connect(String pass) {
                        sendJava(MSG_USER_CONNECT, user, node, pass, newFuture(user, world, ret));
                    }

                    @Override
                    public boolean encrypted() {
                        return data.encrypted;
                    }
                };
                availTargets.add(availTarget);
            }
            rebuildPage(ret.window, linkedTarget, availTargets);
        }));
    }

    private static Page apply() {
        Widget widget = wirelessPageTemplate;
        Widget connectIcon = widget.getWidget("panel_wireless/elem_connected/icon_connect");
        connectIcon.addComponent(new LinkedInfo(null));
        connectIcon.listen(LeftClickEvent.class, (a, event) -> {
            LinkedTarget target = connectIcon.getComponent(LinkedInfo.class).getTarget();
            if (target != null) {
                target.disconnect();
            }
        });
        return new Page("wireless", widget);
    }

    public static void confirm(Widget passBox, AvailTarget target) {
        String password = passBox.getComponent(TextBox.class).content;
        target.connect(password);
        passBox.getComponent(TextBox.class).setContent("");
    }

    private static void sendJava(String msg, Object... pars) {
        NetworkMessage.sendToServer(WirelessNetDelegate.INSTANCE, msg, pars);
    }
}

@SuppressWarnings("unused")
class WirelessNetDelegate {
    public static final WirelessNetDelegate INSTANCE = new WirelessNetDelegate();
    public static final String MSG_FIND_NODES = "find_nodes";
    public static final String MSG_USER_CONNECT = "user_connect";
    public static final String MSG_USER_DISCONNECT = "unlink";

    @StateEventCallback
    private static void init(FMLInitializationEvent ev) {
        NetworkS11n.addDirectInstance(WirelessNetDelegate.INSTANCE);
    }

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

