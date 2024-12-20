package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.CGuiScreenContainer;
import net.minecraft.inventory.Container;

import java.util.Arrays;
import java.util.List;

public class AcademyContainerUI extends CGuiScreenContainer {
    public TechUI.TechUIWidget main;
    public TechUI.InfoArea infoPage;

    public AcademyContainerUI(Container container, TechUI.Page... pages) {
        super(container);
        this.xSize += 31;
        this.ySize += 20;

        List<TechUI.Page> pageList = Arrays.asList(pages);

        main = TechUI.applyJava(pageList);
        main.pos(-18, 0);

        infoPage = new TechUI.InfoArea();
        infoPage.pos(main.transform.width + 7, 5);

        main.addWidget(infoPage);

        gui.addWidget(main);
    }

    public boolean shouldDisplayInventory(TechUI.Page page) {
        return "inv".equals(page.id());
    }

    @Override
    public boolean isSlotActive() {
        return shouldDisplayInventory(main.currentPage());
    }
}
