package cn.academy.internel.ui;

import cn.academy.ability.client.ui.SkillTreeJava;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkillTreeAppUI {
    public static CGuiScreen apply() {
        CGuiScreen ret = new SkillTreeJava.TreeScreen();
        CGui gui = ret.getGui();

        gui.addWidget(SkillTreeJava.initialize(null,gui));

        return ret;
    }
}