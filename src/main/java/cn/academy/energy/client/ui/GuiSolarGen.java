package cn.academy.energy.client.ui;

import cn.academy.Resources;
import cn.academy.block.container.ContainerSolarGen;
import cn.academy.block.tileentity.TileSolarGen;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.academy.core.client.ui.WirelessPage;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.util.HudUtils;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.util.ResourceLocation;


public class GuiSolarGen {
    private static final Widget template = CGUIDocument.read(Resources.getGui("rework/page_solar")).getWidget("main");
    private static final ResourceLocation texture = Resources.getTexture("guis/effect/effect_solar");

    public static AcademyContainerUI apply(ContainerSolarGen container) {
        TileSolarGen tile = container.tile;

        Widget main = template.copy();
        Widget animFrame = main.getWidget("ui_block/anim_frame");
        animFrame.listen(FrameEvent.class, 0, (widget, event) -> {
            double v;
            switch (tile.getStatus()) {
                case STOPPED:
                    v = 1.0 / 3.0;
                    break;
                case STRONG:
                    v = 0.0;
                    break;
                case WEAK:
                    v = 2.0 / 3.0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + tile.getStatus());
            }
            RenderUtils.loadTexture(texture);
            HudUtils.rawRect(0, 0, 0, v, animFrame.transform.width, animFrame.transform.height, 1, 1.0 / 3.0);
        });

        TechUI.Page invPage = InventoryPage.apply(main);
        TechUI.Page wirelessPage = WirelessPage.userPage(tile);

        AcademyContainerUI academyContainerUI = new AcademyContainerUI(container, invPage, wirelessPage);

        academyContainerUI.infoPage.histogramTile(tile).seplineInfo().property("gen_speed", String.format("%.2fIF/T", tile.getGeneration(1024)), null, false, true, null);

        return academyContainerUI;
    }
}
