package cn.academy.internal.terminal.app.settings;

import cn.academy.internal.client.ui.CustomizeUI;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.WidgetContainer;
import cn.lambdalib2.cgui.component.DragBar;
import cn.lambdalib2.cgui.component.ElementList;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.cgui.event.DragEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class SettingsUI extends CGuiScreen {
    static WidgetContainer document = CGUIDocument.read(new ResourceLocation("academy:guis/settings.xml"));
    private static final List<Widget> widgets = new ArrayList<>();

    static {
        SettingsUI.addCallback("edit_ui", () -> Minecraft.getMinecraft().displayGuiScreen(new CustomizeUI()));
    }

    public static void addKey(String name, int value) {
        Widget ret = SettingsUI.document.getWidget("t_key").copy();
        TextBox.get(ret.getWidget("text")).setContent(UIProperty.getDisplayID(name));

        Widget key = ret.getWidget("key");
        key.addComponent(new PropertyElements.EditKey(name, value));
        widgets.add(ret);
    }

    public static void addCallback(String name, Runnable runnable) {
        Widget ret = SettingsUI.document.getWidget("t_callback").copy();
        TextBox.get(ret.getWidget("text")).setContent(UIProperty.getDisplayID(name));
        ret.getWidget("button").listen(LeftClickEvent.class, (w, e) -> runnable.run());
        widgets.add(ret);
    }

    public SettingsUI() {
        initPages();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    private void initPages() {
        Widget main = document.getWidget("main").copy();

        Widget area = main.getWidget("area");

        ElementList list = new ElementList();
        {
            for (Widget widget : widgets) {
                list.addWidget(widget);
            }
        }
        area.addComponent(list);

        Widget bar = main.getWidget("scrollbar");
        bar.listen(DragEvent.class, (w, e) -> list.setProgress((int) (list.getMaxProgress() * DragBar.get(w).getProgress())));

        gui.addWidget(main);
    }
}