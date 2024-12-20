package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerWindGenMain;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;

public class GuiWindGenMain {
    public static AcademyContainerUI apply(ContainerWindGenMain container) {
        int tileWindGenMain = container.tile.getPos().getY();

        TechUI.Page inventoryPage = InventoryPage.apply("windmain");

        AcademyContainerUI academyContainerUI = new AcademyContainerUI(container, inventoryPage);
        academyContainerUI.infoPage.property("altitude", tileWindGenMain, null, false, true, null);

        return new AcademyContainerUI(container, inventoryPage);
    }
}
