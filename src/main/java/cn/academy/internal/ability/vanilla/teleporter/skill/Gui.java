package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.academy.Resources;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.WidgetContainer;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.HudUtils;
import cn.lambdalib2.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.academy.internal.ability.vanilla.teleporter.skill.LTNetDelegate.*;

public class Gui extends CGuiScreen {

    private static final WidgetContainer template = CGUIDocument.read(Resources.getGui("loctele_new"));
    private static final double ElemTimeStep = 0.06;

    public static int dimensionNameMap(int dimID) {
        return DimensionManager.createProviderFor(dimID).getDimension();
    }

    public static class Blend {
        private final double initTime = GameTimer.getTime();
        private final double timeOffset;
        private final double length;

        public Blend(double timeOffset, double length) {
            this.timeOffset = timeOffset;
            this.length = length;
        }

        public float alpha() {
            double dt = (GameTimer.getTime() - initTime) - timeOffset;
            return (float) MathUtils.clampd(0, 1, dt / length);
        }
    }

    public static class DefColors {
        public static final float AlphaNormal = 0.1f;
        public static final float AlphaHighlight = 0.4f;
        public static final Color TextNormal = c(0xffc1cfd5);

        private static Color c(int hex) {
            int a = (hex & 0xff000000) >> 24;
            int r = (hex & 0x00ff0000) >> 16;
            int g = (hex & 0x0000ff00) >> 8;
            int b = (hex & 0x000000ff);
            return new Color(r, g, b, a);
        }
    }

    public static class MessageTab extends Component {
        private static final int textSize = 40;
        private static final int lineHeight = 42;
        private static final int ymargin = 20;
        private static final int xmargin = 20;

        private final IFont.FontOption fontOption = new IFont.FontOption(textSize, IFont.FontAlign.RIGHT, DefColors.TextNormal);
        private final IFont font = Resources.font();

        private List<String> text = new ArrayList<>();

        public MessageTab() {
            super("MessageTab");

            listen(FrameEvent.class, (w, evt) -> {
                for (int i = 0; i < text.size(); i++) {
                    String content = text.get(i);
                    int y = ymargin + lineHeight * i;
                    font.draw(content, widget.transform.width - xmargin, y, fontOption);
                }
            });
        }

        public void updateText(List<String> t) {
            text = t;

            if (t.isEmpty()) {
                widget.transform.doesDraw = false;
            } else {
                widget.transform.doesDraw = true;
                int width = text.stream().mapToInt(s -> (int) font.getTextWidth(s, fontOption)).max().orElse(0) + xmargin * 2;
                int height = text.size() * lineHeight + ymargin * 2;
                widget.size(width, height);
            }
        }
    }

    private final Widget root = template.getWidget("root").copy();
    private final Widget info = root.getWidget("info");
    private final Widget list = root.getWidget("menu/list");

    private final EntityPlayerSP player = Minecraft.getMinecraft().player;

    private Optional<HintMessage> currentMessage = Optional.empty();

    public Gui() {
        // hide templates
        Widget elemTemplate = list.getWidget("elem_template");
        Widget addTemplate = list.getWidget("add_template");

        elemTemplate.transform.doesDraw = false;
        addTemplate.transform.doesDraw = false;

        // blend in menu
        Widget menu = root.getWidget("menu");
        Blend blend = new Blend(0, 0.4);
        double maxHeight = menu.transform.height;
        menu.transform.height = 0;
        menu.listen(FrameEvent.class, () -> menu.transform.height = (float) (blend.alpha() * maxHeight));

        // Initialize info area
        info.addComponent(new MessageTab());
        info.getComponent(MessageTab.class).updateText(new ArrayList<>());

        info.listen(FrameEvent.class, () -> currentMessage.ifPresent(msg -> {
            if (!msg.isAvailable()) {
                setMessage(Optional.empty());
            }
        }));

        LocTeleportData data = LocTeleportData.apply(player);
        updateList(data.locationList);

        getGui().addWidget(root);
    }

    private void wrapBack(Widget ret, int n, List<String> msg) {
        ret.removeComponent(Tint.class);

        Blend blend = new Blend(n * ElemTimeStep, 0.2);

        boolean[] lastHovering = {false};

        ret.listen(FrameEvent.class, () -> {
            boolean hovering = ret.isPointWithin(ret.getGui().getMouseX(), ret.getGui().getMouseY());

            if (!lastHovering[0] && hovering) {
                lastHovering[0] = true;
                setMessage(Optional.of(new HintMessage() {
                    @Override
                    public boolean isAvailable() {
                        return lastHovering[0];
                    }

                    @Override
                    public double getYpos() {
                        return ret.y;
                    }

                    @Override
                    public List<String> getMessage() {
                        return msg;
                    }
                }));
            }

            double alpha0 = blend.alpha() * (hovering ? DefColors.AlphaHighlight : DefColors.AlphaNormal);
            Colors.bindToGL(Colors.whiteBlend((float) alpha0));
            HudUtils.colorRect(0, 0, ret.transform.width, ret.transform.height);

            lastHovering[0] = hovering;
        });
    }

    private void wrapButton(Widget target, int n, double offset, Runnable clickCallback) {
        Color color = new Color(DefColors.TextNormal);
        color.setAlpha(0);

        target.getComponent(DrawTexture.class).color = color;
        Blend blend = new Blend(n * ElemTimeStep + offset, 0.1);
        target.listen(FrameEvent.class, (a, event) -> {
            double a0 = event.hovering ? 1.0 : 0.7;
            color.setAlpha((int) (a0 * blend.alpha() * 255));
        });
        target.listen(LeftClickEvent.class, clickCallback);
    }

    private void setMessage(Optional<HintMessage> value) {
        double ypos = value.map(HintMessage::getYpos).orElse(0.0);
        List<String> texts = value.map(HintMessage::getMessage).orElse(new ArrayList<>());

        currentMessage = value;
        gui.moveWidgetToAbsPos(info, info.x, (float) ypos);
        info.getComponent(MessageTab.class).updateText(texts);
        gui.updateWidget(info);
    }

    private void updateList(List<Location> locations) {
        list.removeComponent(ElementList.class);

        ElementList compList = new ElementList();
        compList.spacing = 2;

        for (Location l : locations) {
            compList.addWidget(newElem(l, compList.getSubWidgets().size()));
        }

        compList.addWidget(newAdd(compList.size()));

        list.addComponent(compList);
    }

    private Widget newElem(Location location, int count) {
        Widget ret = list.getWidget("elem_template").copy();
        ret.transform.doesDraw = true;

        TextBox textBox = ret.getWidget("text").getComponent(TextBox.class);
        textBox.setContent(location.name);

        String stat = LocationTeleport.getPerformStat(player, location);
        double cp = LocationTeleport.getConsumption(player, location)[1];

        List<String> message = new ArrayList<>();
        int dimensionName = dimensionNameMap(location.dim);
        message.add(dimensionName + " (#" + location.dim + ")");
        message.add(String.format("(%.0f, %.0f, %.0f)", location.x, location.y, location.z));
        message.add(String.format("%.0f CP", cp));

        if (stat != null) {
            message.add(stat);
        }

        wrapBack(ret, count, message);

        if (stat == null) {
            wrapButton(ret.getWidget("btn_teleport"), count, 0.03, () -> {
                mc.displayGuiScreen(null);
                ACSounds.playClient(player, "tp.tp", SoundCategory.AMBIENT, 0.5f);
                send(MSG_PERFORM, player, location);
            });
        } else {
            ret.removeWidget("btn_teleport");
            ret.getWidget("text").getComponent(TextBox.class).option.color.set(0xa2, 0xa2, 0xa2);
        }

        wrapButton(ret.getWidget("btn_remove"), count, 0.05, () -> send(MSG_REMOVE, player, location.id, Future.create(this::updateList)));

        Widget wid = ret.getWidget("text");
        TextBox text = wid.getComponent(TextBox.class);
        text.option.color.setAlpha(0);

        Blend blend = new Blend(count * ElemTimeStep + 0.1, 0.1);
        wid.listen(FrameEvent.class, () -> text.option.color.setAlpha((int) (blend.alpha() * 255)));

        return ret;
    }

    Widget inputText;
    TextBox textBox;

    private Widget newAdd(int count) {
        Widget ret = list.getWidget("add_template").copy();

        List<String> message = new ArrayList<>();
        int dimID = player.getEntityWorld().provider.getDimension();
        int name = dimensionNameMap(dimID);

        message.add(name + " (#" + dimID + ")");
        message.add(String.format("(%.0f, %.0f, %.0f)", player.posX, player.posY, player.posZ));

        wrapBack(ret, count, message);

        Blend blend = new Blend(count * ElemTimeStep, 0.2);
        inputText = ret.getWidget("input_text");
        textBox = inputText.getComponent(TextBox.class);
        textBox.option.color.setAlpha(0);

        inputText.listen(FrameEvent.class, () -> textBox.option.color.setAlpha((int) (blend.alpha() * (inputText.isFocused() ? 0.8 : 0.4) * 255)));

        ret.listen(LeftClickEvent.class, () -> {
            if (!inputText.isFocused()) {
                textBox.allowEdit = true;
                textBox.setContent("");
                getGui().gainFocus(inputText);
            }
        });

        ret.listen(TextBox.ConfirmInputEvent.class, this::confirmInput);

        wrapButton(ret.getWidget("btn_confirm"), count, 0.0, this::confirmInput);

        return ret;
    }

    private void confirmInput() {
        String content = inputText.getComponent(TextBox.class).content;
        int maxLength = Math.min(content.length(), 16);
        String result = content.substring(0, maxLength);

        send(MSG_ADD, player, result, Future.create(this::updateList));

        gui.removeFocus();
        textBox.allowEdit = false;
        textBox.setContent("Add...");
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public interface HintMessage {
        double getYpos();

        List<String> getMessage();

        boolean isAvailable();
    }
}