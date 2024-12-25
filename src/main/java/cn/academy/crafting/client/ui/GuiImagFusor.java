package cn.academy.crafting.client.ui;

import cn.academy.Resources;
import cn.academy.block.container.ContainerImagFusor;
import cn.academy.block.tileentity.TileImagFusor;
import cn.academy.core.client.ui.AcademyContainerUI;
import cn.academy.core.client.ui.HistElement;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.Page;
import cn.academy.crafting.ImagFusorRecipes;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.cgui.loader.CGUIDocument;

public class GuiImagFusor {
    private static final Widget template = CGUIDocument.read(Resources.getGui("rework/page_imagfusor")).getWidget("main");

    public static AcademyContainerUI apply(ContainerImagFusor container) {
        TileImagFusor tile = container.tile;

        Page invPage = InventoryPage.apply(template);

        AcademyContainerUI ret = new AcademyContainerUI(container, invPage);

        {
            Widget progWidget = invPage.getWindow().getWidget("progress");
            ProgressBar bar = progWidget.getComponent(ProgressBar.class);

            bar.progress = tile.getWorkProgress();
        }

        {
            Widget reqWidget = invPage.getWindow().getWidget("text_imagneeded");
            TextBox text = reqWidget.getComponent(TextBox.class);

            text.content = "IDLE";

            ImagFusorRecipes.IFRecipe recipe = tile.getCurrentRecipe();
            text.setContent(recipe == null ? "IDLE" : String.valueOf(recipe.consumeLiquid));
        }
        ret.infoPage.histogram(HistElement.histEnergy(tile.getEnergy(), tile.getMaxEnergy()), HistElement.histPhaseLiquid(tile.getLiquidAmount(), tile.getTankSize()));

        return ret;
    }
}