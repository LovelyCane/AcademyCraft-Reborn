package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerWindGenMain;
import cn.academy.core.client.ui.ContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.Page;

public class GuiWindGenMain {
    public static ContainerUI apply(ContainerWindGenMain container) {
        int tileWindGenMain = container.tile.getPos().getY();

        Page inventoryPage = InventoryPage.apply("windmain");

        ContainerUI academyContainerUI = new ContainerUI(container, inventoryPage);
        academyContainerUI.infoPage.property("altitude", tileWindGenMain, null, false, true, null);

        return new ContainerUI(container, inventoryPage);
    }
}
