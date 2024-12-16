package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerWindGenMain;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import scala.collection.JavaConversions;

import java.util.Collections;

public class GuiWindGenMain {
    public static TechUI.ContainerUI apply(ContainerWindGenMain container) {
        int tileWindGenMain = container.tile.getPos().getY();
        TechUI.Page inventoryPage = InventoryPage.apply("windmain");
        return new TechUI.ContainerUI(container, JavaConversions.asScalaBuffer(Collections.singletonList(inventoryPage)).toSeq());
    }
}
