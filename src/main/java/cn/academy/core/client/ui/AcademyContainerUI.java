package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.CGuiScreenContainer;
import net.minecraft.inventory.Container;

public class AcademyContainerUI extends CGuiScreenContainer {
    public AcademyContainerUI(Container container) {
        super(container);
        xSize += 31;
        ySize += 20;
    }
}
