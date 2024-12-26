package cn.academy.core.client.ui;

import cn.academy.util.LocalHelper;
import cn.lambdalib2.cgui.CGuiScreenContainer;
import net.minecraft.inventory.Container;

public class ContainerUI extends CGuiScreenContainer {
    public static final LocalHelper local = LocalHelper.at("ac.gui.common");
    public static final LocalHelper localSep = local.subPath("sep");
    public static final LocalHelper localHist = local.subPath("hist");
    public static final LocalHelper localProperty = local.subPath("prop");

    public TechUIWidget main;
    public InfoArea infoPage;

    public ContainerUI(Container container, Page... pages) {
        super(container);
        this.xSize += 31;
        this.ySize += 20;

        main = new TechUIWidget(pages);
        main.pos(-18, 0);

        infoPage = new InfoArea();
        infoPage.pos(main.transform.width + 7, 5);

        main.addWidget(infoPage);

        gui.addWidget(main);
    }

    public boolean shouldDisplayInventory(Page page) {
        return "inv".equals(page.id);
    }

    @Override
    public boolean isSlotActive() {
        return shouldDisplayInventory(main.currentPage);
    }
}
