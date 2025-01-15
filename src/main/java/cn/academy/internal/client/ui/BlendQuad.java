package cn.academy.internal.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.component.Component;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.HudUtils;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public class BlendQuad extends Component {
    private final ResourceLocation blendQuadTex = Resources.getTexture("guis/blend_quad");
    private final ResourceLocation lineTex = Resources.getTexture("guis/line");
    private double margin = 4;
    private Color color = Colors.monoBlend(0.0f, 0.5f);

    public BlendQuad() {
        this("BlendQuad");
    }

    public BlendQuad(String name) {
        super(name);
        this.listen(FrameEvent.class, (widget, event) -> {
            RenderUtils.loadTexture(blendQuadTex);
            Colors.bindToGL(color);

            double[] xs = new double[]{0 - margin, 0, widget.transform.width, widget.transform.width + margin};
            double[] ys = new double[]{0 - margin, 0, widget.transform.height, widget.transform.height + margin};

            GL11.glBegin(GL11.GL_QUADS);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    quad(i, j, xs[i], ys[j], xs[i + 1], ys[j + 1]);
                }
            }
            GL11.glEnd();

            GL11.glColor4d(1, 1, 1, 1);
            RenderUtils.loadTexture(lineTex);

            double mrg = 3.2;
            HudUtils.rect(-mrg, -8.6, widget.transform.width + mrg * 2, 12);
            HudUtils.rect(-mrg, widget.transform.height - 2, widget.transform.width + mrg * 2, 8);
        });
    }

    private void quad(int col, int row, double x0, double y0, double x1, double y1) {
        double u = col / 3.0;
        double v = row / 3.0;
        double step = 1.0 / 3.0;

        GL11.glTexCoord2d(u, v);
        GL11.glVertex2d(x0, y0);

        GL11.glTexCoord2d(u, v + step);
        GL11.glVertex2d(x0, y1);

        GL11.glTexCoord2d(u + step, v + step);
        GL11.glVertex2d(x1, y1);

        GL11.glTexCoord2d(u + step, v);
        GL11.glVertex2d(x1, y0);
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
