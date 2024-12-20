package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerPhaseGen;
import cn.academy.block.tileentity.TilePhaseGen;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.academy.core.client.ui.WirelessPage;

public class GuiPhaseGen {
    public static AcademyContainerUI apply(ContainerPhaseGen containerPhaseGen) {
        TilePhaseGen tile = containerPhaseGen.tile;
        TechUI.Page inventoryPage = InventoryPage.apply("phasegen");
        TechUI.Page wirelessPage = WirelessPage.userPage(tile);

        return new AcademyContainerUI(containerPhaseGen, inventoryPage, wirelessPage);
    }
}
