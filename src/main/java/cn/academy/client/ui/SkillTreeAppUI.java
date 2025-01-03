package cn.academy.client.ui;

import cn.academy.ability.client.ui.Common;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkillTreeAppUI {
    public static CGuiScreen apply() {
        CGuiScreen ret = new Common.TreeScreen();
        CGui gui = ret.getGui();

        gui.addWidget(Common.initialize(null,gui));

        return ret;
    }
}