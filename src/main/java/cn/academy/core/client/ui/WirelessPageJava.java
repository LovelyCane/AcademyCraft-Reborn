package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.academy.energy.api.block.IWirelessNode;
import cn.academy.util.LocalHelper;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.GainFocusEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.event.LostFocusEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.Colors;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static cn.academy.core.client.ui.WirelessNetDelegateJava.MSG_USER_DISCONNECT_JAVA;

public class WirelessPageJava {
    private static final Widget wirelessPageTemplate = CGUIDocument.read(Resources.getGui("rework/page_wireless")).getWidget("main");
    private static final ResourceLocation connectedIcon = Resources.getTexture("guis/icons/icon_connected");
    private static final ResourceLocation unconnectedIcon = Resources.getTexture("guis/icons/icon_unconnected");
    public static final String MSG_FIND_NODES = "find_nodes";
    public static final String MSG_USER_CONNECT = "user_connect";
    public static final String MSG_USER_DISCONNECT = "unlink";
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
        private Optional<LinkedTarget> target;

        public LinkedInfo(Optional<LinkedTarget> target) {
            super("LinkedInfo");
            this.target = target;
        }

        public Optional<LinkedTarget> getTarget() {
            return target;
        }

        public void setTarget(Optional<LinkedTarget> target) {
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
            connectElem.getWidget("icon_connect").getComponent(LinkedInfo.class).setTarget(Optional.of(linked));
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
        Page ret = WirelessPageJava.apply();
        World world = user.getWorld();

        rebuild(user, world, ret);
        return ret;
    }

    public static Future<Boolean> newFuture(TileEntity user, World world, Page ret) {
        return Future.create(aBoolean -> rebuild(user, world, ret));
    }

    private static void rebuild(TileEntity user, World world, Page ret) {
        send(MSG_FIND_NODES, user, Future.create((Consumer<UserResult>) result -> {
            LinkedTarget linkedTarget = new LinkedTarget() {
                @Override
                public void disconnect() {
                    sendJava(MSG_USER_DISCONNECT_JAVA, user, newFuture(user, world, ret));
                }

                @Override
                public String name() {
                    IWirelessNode node = result.linked() == null ? null : (IWirelessNode) result.linked().tile(world).get();
                    return node == null ? null : node.getNodeName();
                }
            };

            List<AvailTarget> availTargets = new ArrayList<>();
            for (NodeData data : result.avail()) {
                IWirelessNode node = (IWirelessNode) data.tile(world).get();

                AvailTarget availTarget = new AvailTarget() {
                    @Override
                    public String name() {
                        return node.getNodeName();
                    }

                    @Override
                    public void connect(String pass) {
                        send(MSG_USER_CONNECT, user, node, pass, newFuture(user, world, ret));
                    }

                    @Override
                    public boolean encrypted() {
                        return data.encrypted();
                    }
                };
                availTargets.add(availTarget);
            }

            rebuildPage(ret.window, linkedTarget, availTargets);
        }));
    }

    private static Page apply() {
        Widget widget = wirelessPageTemplate;
        TechUI.breathe(widget.getWidget("icon_logo"));

        Widget wirelessPanel = widget.getWidget("panel_wireless");
        Widget wlist = wirelessPanel.getWidget("zone_elementlist");

        ElementList elist = wlist.getComponent(ElementList.class);

        wirelessPanel.getWidget("btn_arrowup").listen(LeftClickEvent.class, (a, event) -> elist.progressLast());
        wirelessPanel.getWidget("btn_arrowdown").listen(LeftClickEvent.class, (a, event) -> elist.progressNext());

        Widget connectIcon = widget.getWidget("panel_wireless/elem_connected/icon_connect");
        connectIcon.addComponent(new LinkedInfo(Optional.empty()));

        connectIcon.listen(LeftClickEvent.class, (a, event) -> {
            Optional<LinkedTarget> target = connectIcon.getComponent(LinkedInfo.class).getTarget();
            target.ifPresent(LinkedTarget::disconnect);
        });

        return new Page("wireless", widget);
    }

    public static void confirm(Widget passBox, AvailTarget target) {
        String password = passBox.getComponent(TextBox.class).content;
        target.connect(password);
        passBox.getComponent(TextBox.class).setContent("");
    }

    private static void send(String msg, Object... pars) {
        NetworkMessage.sendToServer(WirelessNetDelegate$.MODULE$, msg, pars);
    }
    private static void sendJava(String msg, Object... pars) {
        NetworkMessage.sendToServer(WirelessNetDelegateJava.INSTANCE, msg, pars);
    }
}
