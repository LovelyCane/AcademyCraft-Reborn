package cn.academy.crafting.client.ui;

import cn.academy.Resources;
import cn.academy.block.container.ContainerMetalFormer;
import cn.academy.block.tileentity.TileMetalFormer;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
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
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.util.Colors;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Supplier;

public class GuiMetalFormer {
    private static final Widget template = CGUIDocument.read(Resources.getGui("rework/page_metalformer")).getWidget("main");

    public static AcademyContainerUI apply(ContainerMetalFormer container) {
        final TileMetalFormer tile = container.tile;

        {
            updateModeTexture(tile.mode, template);

            template.getWidget("progress").listen(FrameEvent.class, (Widget w, FrameEvent evt) -> {
                ProgressBar progressBar = w.getComponent(ProgressBar.class);
                progressBar.progress = tile.getWorkProgress();
            });

            template.getWidget("btn_left").listen(LeftClickEvent.class, () -> handleAlt(-1, tile));

            template.getWidget("btn_right").listen(LeftClickEvent.class, () -> handleAlt(1, tile));

            {
                IFont.FontOption option = new IFont.FontOption(10, IFont.FontAlign.CENTER, Colors.fromHexColor(0xaaffffff));
                template.getWidget("icon_mode").listen(FrameEvent.class, (Widget w, FrameEvent evt) -> {
                    if (evt.hovering) {
                        TechUI.drawTextBox(tile.mode.toString(), option, 6, -10, Float.MAX_VALUE);
                    }
                });
            }
        }

        TechUI.Page invPage = InventoryPage.apply(template);
        AcademyContainerUI ret = new AcademyContainerUI(container, invPage);

        ret.infoPage.histogram1(tile);

        return ret;
    }

    public static void updateModeTexture(TileMetalFormer.Mode mode, Widget invWidget) {
        invWidget.getWidget("icon_mode").getComponent(DrawTexture.class).texture = mode.texture;
    }

    public static Supplier<Void> handleAlt(int dir, TileMetalFormer tile) {
        return () -> {
            send(MFNetDelegate.MSG_ALTERNATE, tile, dir, Future.create(future -> {
                updateModeTexture(tile.mode, template);
            }));
            return null;
        };
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

    @Listener(channel = MSG_ALTERNATE, side = {Side.SERVER})
    public void alternate(TileMetalFormer tile, int dir, Future<TileMetalFormer.Mode> fut) {
        tile.cycleMode(dir);
        fut.sendResult(tile.mode);
    }
}
