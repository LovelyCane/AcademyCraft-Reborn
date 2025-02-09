package cn.academy.internal.client.ui;

import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkillTreeAppUI {
    public static CGuiScreen apply() {
        CGuiScreen ret = new SkillTree.TreeScreen();
        CGui gui = ret.getGui();

        gui.addWidget(SkillTree.initialize(null,gui));

        return ret;
    }
}