package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerNode;
import cn.academy.block.tileentity.TileNode;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.lambdalib2.cgui.Widget;

public class GuiNode {
    static int load = 1;

    public static AcademyContainerUI apply(ContainerNode container) {
        TileNode tile = container.tile;

        TechUI.Page invPage = InventoryPage.apply("node");

        Widget animArea = new Widget().pos(42, 35.5f).size(186, 75).scale(0.5f);

        invPage.window().addWidget(animArea);

        AcademyContainerUI ret = new AcademyContainerUI(container, invPage);

        {
            ret.infoPage
                    .histogramNode(tile, load)
                    .seplineInfo()
                    .property("range", tile.getRange(), null, false, true, null)
                    .property("owner", tile.getPlacerName(), null, false, true, null);
        }

        return ret;
    }
}