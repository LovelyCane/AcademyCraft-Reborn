package cn.academy.ability.client.ui;

import cn.academy.ability.develop.IDeveloper;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DeveloperUI {
    public static <T extends IDeveloper> CGuiScreen apply(T tile) {
        SkillTreeJava.TreeScreen ret = new SkillTreeJava.TreeScreen() {
            @Override
            public void onGuiClosed() {
                tile.onGuiClosed();
            }
        };

        final CGui gui = ret.getGui();

        Runnable build = () -> {
            gui.clear();
            gui.addWidget("main", SkillTreeJava.initialize(tile, gui));
        };

        build.run();

        return ret;
    }
}
