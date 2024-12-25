package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerNode;
import cn.academy.block.tileentity.TileNode;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.HistElement;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.Page;
import cn.lambdalib2.cgui.Widget;

public class GuiNode {
    public static AcademyContainerUI apply(ContainerNode container) {
        TileNode tile = container.tile;

        Page invPage = InventoryPage.apply("node");

        Widget animArea = new Widget().pos(42, 35.5f).size(186, 75).scale(0.5f);

        invPage.getWindow().addWidget(animArea);

        AcademyContainerUI ret = new AcademyContainerUI(container, invPage);

        {
            ret.infoPage
                    .histogram(HistElement.histEnergy(tile.getEnergy(), tile.getMaxEnergy()), HistElement.histCapacity(tile.getCapacity(), tile.getMaxEnergy()))
                    .sepline("info")
                    .property("range", tile.getRange(), null, false, true).property("owner", tile.getPlacerName(), null, false, true);
        }
        return ret;
    }
}