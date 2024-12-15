package cn.lambdalib2.cgui.event;

import cn.lambdalib2.cgui.Widget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fired on CGui when a new widget is added into it.
 */
@SideOnly(Side.CLIENT)
public class AddWidgetEvent implements GuiEvent {
    
    public final Widget widget;
    
    public AddWidgetEvent(Widget w) {
        widget = w;
    }
    
}
