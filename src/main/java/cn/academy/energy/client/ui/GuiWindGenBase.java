package cn.academy.energy.client.ui;

import cn.academy.Resources;
import cn.academy.block.container.ContainerWindGenBase;
import cn.academy.block.tileentity.TileWindGenBase;
import cn.academy.core.client.ui.*;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.util.Colors;

import static cn.academy.block.tileentity.TileWindGenBase.Completeness.*;

public class GuiWindGenBase {
    private static final Widget main = CGUIDocument.read(Resources.getGui("rework/page_windbase")).getWidget("main");

    public static ContainerUI apply(ContainerWindGenBase containerWindGenBase) {
        TileWindGenBase tileWindGenBase = containerWindGenBase.tile;

        float a0 = 0.2f, a1 = 0.6f, a2 = 1.0f;
        float amain, amiddle;

        TileWindGenBase.Completeness completeness = tileWindGenBase.getCompleteness();

        amain = (completeness == COMPLETE) ? a2 : (completeness == COMPLETE_NOT_WORKING) ? a1 : a0;
        amiddle = (completeness == BASE_ONLY) ? a0 : a2;

        Widget uiBlock = main.getWidget("ui_block");
        Widget iconMain = uiBlock.getWidget("icon_main");
        Widget iconMiddle = uiBlock.getWidget("icon_middle");

        iconMain.getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(amain));
        iconMiddle.getComponent(DrawTexture.class).color.setAlpha(Colors.f2i(amiddle));

        Page inventoryPage = InventoryPage.apply(main);
        Page wirelessPage = WirelessPage.userPage(tileWindGenBase);

        ContainerUI academyContainerUI = new ContainerUI(containerWindGenBase, inventoryPage, wirelessPage);

        HistElement elems = HistUtils.histBuffer(tileWindGenBase::getEnergy, tileWindGenBase.bufferSize);

        academyContainerUI.infoPage.histogram(elems).sepline("info").property("altitude", tileWindGenBase.getPos().getY(), null, false, true, null);

        return academyContainerUI;
    }
}
