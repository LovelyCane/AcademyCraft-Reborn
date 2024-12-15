package cn.lambdalib2.cgui;

import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.render.font.IFont.FontOption;
import cn.lambdalib2.render.font.TrueTypeFont;
import com.google.common.base.Joiner;
import org.lwjgl.input.Keyboard;

import java.util.stream.Collectors;

/**
 * This widget prints hierarchy debug information of widget last time mouse hovered when press LSHIFT+D.
 */
public class HierarchyDebugger extends Widget {

    Widget focus;

    {
        listen(FrameEvent.class, (w, e) -> {
            boolean test = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_D);
            if (test) {
                focus = getGui().getHoveringWidget();
            }

            if (focus != null) {
                drawHierarchy(10, 10, focus, focus.getFullName(), 9);
            }
        });
    }

    private static final FontOption option = new FontOption(10);
    private float drawHierarchy(float x, float y, Widget w, String name, float fsize) {
        IFont font = TrueTypeFont.defaultFont;

        StringBuilder sb = new StringBuilder();
        sb.append("·").append(name).append(" [");

        String compns = Joiner.on(',').join(w.getComponentList().stream()
                .filter(c -> !(c instanceof Transform))
                .map(c -> c.name)
                .collect(Collectors.toList()));
        sb.append(compns);
        sb.append("]");

        option.fontSize = fsize;
        font.draw(sb.toString(), x, y, option);

        for (Widget sub : w.getDrawList()) {
            y += fsize * 1.1;

            y = drawHierarchy(x + fsize * 0.5f, y, sub, sub.getName(), fsize * 0.9f);
        }

        return y;
    }

}
