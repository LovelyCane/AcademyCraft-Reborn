package cn.academy.internal.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.render.font.IFont.FontOption;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.HudUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

@SideOnly(Side.CLIENT)
public class UIEffectsHelper {
    public static void drawTextBox(String content, FontOption option, float x, float y, float limit) {
        final int wmargin = 5;
        final int hmargin = 2;

        IFont font = Resources.font();
        IFont.Extent extent = font.drawSeperated_Sim(content, limit, option);

        GL11.glColor4f(0, 0, 0, 0.5f);
        HudUtils.colorRect(x - extent.width * option.align.lenOffset, y, extent.width + wmargin * 2 + 2, extent.height + hmargin * 2);

        GL11.glColor4f(1, 1, 1, 0.8f);
        font.drawSeperated(content, x + wmargin, y + hmargin, limit, option);

        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void breathe(Widget widget) {
        Optional.ofNullable(widget.getComponent(DrawTexture.class)).ifPresent(tex -> widget.listen(FrameEvent.class, (w, event) -> tex.color.setAlpha(Colors.f2i(breatheAlpha()))));
    }

    /**
     * A global alpha value generator for producing uniform breathing effect.
     */
    private static float breatheAlpha() {
        double time = GameTimer.getTime();
        double sin = (1 + Math.sin(time / 0.8)) * 0.5;

        return (float) (0.675 + sin * 0.175);
    }
}
