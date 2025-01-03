package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryPage {
    private static final Widget template = CGUIDocument.read(new ResourceLocation("academy:guis/rework/page_inv.xml")).getWidget("main");

    public static Page apply(String name) {
        Page ret = InventoryPage.apply(template.copy());
        ret.window.getWidget("ui_block").getComponent(DrawTexture.class).setTex(Resources.getTexture("guis/ui/ui_" + name));
        return ret;
    }

    public static Page apply(Widget ret) {
        for (Widget w : ret.getDrawList()) {
            if (w.getName().startsWith("ui_")) {
                UIEffectsHelper.breathe(w);
            }
        }
        return new Page("inv", ret);
    }
}
