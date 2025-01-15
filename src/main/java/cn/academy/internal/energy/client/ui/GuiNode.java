package cn.academy.internal.energy.client.ui;

import cn.academy.internal.inventory.ContainerNode;
import cn.academy.internal.tileentity.TileNode;
import cn.academy.internal.client.ui.*;
import cn.lambdalib2.cgui.Widget;

public class GuiNode {
    public static ContainerUI apply(ContainerNode container) {
        TileNode tile = container.tile;

        Page invPage = InventoryPage.apply("node");

        Widget animArea = new Widget().pos(42, 35.5f).size(186, 75).scale(0.5f);

        invPage.window.addWidget(animArea);

        ContainerUI ret = new ContainerUI(container, invPage);

        HistElement elems1 = HistUtils.histEnergy(tile::getEnergy, tile.getMaxEnergy());
        HistElement elems2 = HistUtils.histCapacity(tile::getCapacity, tile.getCapacity());

        ret.infoPage
                .histogram(elems1, elems2)
                .sepline("info")
                .property("range", tile.getRange(), null, false, true, null)
                .property("owner", tile.getPlacerName(), null, false, true, null);

        return ret;
    }
}