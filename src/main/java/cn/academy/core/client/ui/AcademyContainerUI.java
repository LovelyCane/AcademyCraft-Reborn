package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.CGuiScreenContainer;
import net.minecraft.inventory.Container;

public class AcademyContainerUI extends CGuiScreenContainer {
    public TechUIWidget main;
    public InfoArea infoPage;

    public AcademyContainerUI(Container container, Page... pages) {
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
        return "inv".equals(page.getId());
    }

    @Override
    public boolean isSlotActive() {
        return shouldDisplayInventory(main.getCurrentPage());
    }
}
