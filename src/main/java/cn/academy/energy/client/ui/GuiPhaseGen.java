package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerPhaseGen;
import cn.academy.block.tileentity.TilePhaseGen;
import cn.academy.core.client.ui.*;

public class GuiPhaseGen {
    public static AcademyContainerUI apply(ContainerPhaseGen containerPhaseGen) {
        TilePhaseGen tile = containerPhaseGen.tile;
        Page inventoryPage = InventoryPage.apply("phasegen");
        Page wirelessPage = WirelessPage.userPage(tile);

        return new AcademyContainerUI(containerPhaseGen, inventoryPage, wirelessPage);
    }
}
