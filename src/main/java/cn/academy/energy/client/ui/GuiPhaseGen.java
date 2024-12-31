package cn.academy.energy.client.ui;

import cn.academy.block.container.ContainerPhaseGen;
import cn.academy.block.tileentity.TilePhaseGen;
import cn.academy.core.client.ui.*;

public class GuiPhaseGen {
    public static ContainerUI apply(ContainerPhaseGen containerPhaseGen) {
        TilePhaseGen tilePhaseGen = containerPhaseGen.tile;
        Page inventoryPage = InventoryPage.apply("phasegen");
        Page wirelessPage = WirelessPage.userPage(tilePhaseGen);

        ContainerUI ret = new ContainerUI(containerPhaseGen, inventoryPage, wirelessPage);

        HistElement element1 = HistUtils.histEnergy(tilePhaseGen::getEnergy, tilePhaseGen.bufferSize);
        HistElement element2 = HistUtils.histPhaseLiquid(() -> (double) tilePhaseGen.getLiquidAmount(), tilePhaseGen.getTankSize());

        ret.infoPage.histogram(element1, element2);

        return ret;
    }
}
