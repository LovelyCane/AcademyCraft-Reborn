package cn.academy.ability.client.ui;

import cn.academy.ability.develop.IDeveloper;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class DeveloperUI {
    public static CGuiScreen apply(IDeveloper tile) {
        Common.TreeScreen ret = new Common.TreeScreen() {

            @Override
            public void onGuiClosed() {
                tile.onGuiClosed();
            }

            @Override
            public void keyTyped(char ch, int key) throws IOException {
                if (key == Keyboard.KEY_ESCAPE) {
                    Widget linkPage = gui.getWidget("link_page");
                    if (linkPage != null) {
                        Common.Cover cover = linkPage.getComponent(Common.Cover.class);
                        if (cover != null) {
                            cover.end();
                        }
                    } else {
                        super.keyTyped(ch, key);
                    }
                } else {
                    super.keyTyped(ch, key);
                }
            }
        };

        CGui gui = ret.getGui();

        Runnable build = () -> {
            gui.clear();
            gui.addWidget("main", Common.initialize(tile,gui));
        };

        gui.listen(Common.RebuildEvent.class, (w, event) -> build.run());

        build.run();

        return ret;
    }
}
