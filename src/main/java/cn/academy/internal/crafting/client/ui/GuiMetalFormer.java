package cn.academy.internal.crafting.client.ui;

import cn.academy.Resources;
import cn.academy.internal.inventory.ContainerMetalFormer;
import cn.academy.internal.tileentity.TileMetalFormer;
import cn.academy.internal.client.ui.*;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.s11n.network.Future;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.util.Colors;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class GuiMetalFormer {
    private static final Widget template = CGUIDocument.read(Resources.getGui("rework/page_metalformer")).getWidget("main");

    public static ContainerUI apply(ContainerMetalFormer container) {
        final TileMetalFormer tile = container.tile;

        template.getWidget("icon_mode").getComponent(DrawTexture.class).texture = tile.mode.texture;

        template.getWidget("progress").listen(FrameEvent.class, (Widget w, FrameEvent evt) -> w.getComponent(ProgressBar.class).progress = tile.getWorkProgress());

        template.getWidget("btn_left").listen(LeftClickEvent.class, () -> send(MFNetDelegate.MSG_ALTERNATE, tile, -1, Future.create(future -> template.getWidget("icon_mode").getComponent(DrawTexture.class).texture = tile.mode.texture)));

        template.getWidget("btn_right").listen(LeftClickEvent.class, () -> send(MFNetDelegate.MSG_ALTERNATE, tile, 1, Future.create(future -> template.getWidget("icon_mode").getComponent(DrawTexture.class).texture = tile.mode.texture)));

        IFont.FontOption option = new IFont.FontOption(10, IFont.FontAlign.CENTER, Colors.fromHexColor(0xaaffffff));
        template.getWidget("icon_mode").listen(FrameEvent.class, (Widget w, FrameEvent evt) -> {
            if (evt.hovering) {
                UIEffectsHelper.drawTextBox(tile.mode.toString(), option, 6, -10, Float.MAX_VALUE);
            }
        });

        ContainerUI ret = new ContainerUI(container, InventoryPage.apply(template), WirelessPage.userPage(tile));

        ret.infoPage.histogram(HistUtils.histBuffer(tile::getEnergy, tile.getMaxEnergy()));

        return ret;
    }

    private static void send(String channel, Object... args) {
        NetworkMessage.sendToServer(MFNetDelegate.INSTANCE, channel, args);
    }
}

class MFNetDelegate {
    public static final MFNetDelegate INSTANCE = new MFNetDelegate();
    public static final String MSG_ALTERNATE = "alt";

    @StateEventCallback
    public static void init(FMLInitializationEvent ev) {
        NetworkS11n.addDirectInstance(INSTANCE);
    }

    @NetworkMessage.Listener(channel = MSG_ALTERNATE, side = {Side.SERVER})
    public void alternate(TileMetalFormer tile, int dir, Future<TileMetalFormer.Mode> fut) {
        tile.cycleMode(dir);
        fut.sendResult(tile.mode);
    }
}