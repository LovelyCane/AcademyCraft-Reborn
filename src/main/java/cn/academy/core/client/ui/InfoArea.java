package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.util.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static cn.academy.Resources.newTextBox;
import static org.lwjgl.opengl.GL11.glColor4d;

public class InfoArea extends Widget {
    private final ResourceLocation blendQuadTex = Resources.getTexture("guis/blend_quad");
    private final ResourceLocation lineTex = Resources.getTexture("guis/line");

    private float expectWidth = 100.0f;
    private float expectHeight = 0f;

    private final ResourceLocation histogramTex = Resources.getTexture("guis/histogram");

    private double lastFrameTime = GameTimer.getTime();

    private final List<Widget> elements = new ArrayList<>();

    double dt = Math.min(GameTimer.getTime() - lastFrameTime, 0.5);

    public InfoArea() {
        this.addComponent(new BlendQuad());
        this.listen(FrameEvent.class, () -> {
            transform.width = expectWidth;
            transform.height = expectHeight;
            lastFrameTime = GameTimer.getTime();
        });
    }

    public float move(float fr, float to) {
        float max = (float) dt * 500;
        float delta = to - fr;
        return fr + Math.min(max, Math.abs(delta)) * Math.signum(delta);
    }

    // soon
    public <T> InfoArea property(String key, T value,
                                 Consumer<String> editCallback,
                                 boolean password,
                                 boolean colorChange) {

        return this;
    }

    public InfoArea histogram(HistElement... elems) {
        Widget widget = new Widget().size(210, 210).scale(0.4f)
                .addComponent(new DrawTexture(histogramTex));
        for (int idx = 0; idx < elems.length; idx++) {
            HistElement elem = elems[idx];

            Widget bar = new Widget().size(16, 120).pos(56 + idx * 40, 78);
            ProgressBar progress = new ProgressBar();
            progress.color = elem.getColor();
            progress.dir = ProgressBar.Direction.UP;

            bar.listen(FrameEvent.class, (w, event) -> progress.progress = MathUtils.clampd(0.03, 1, elem.getValue()));
            bar.addComponent(progress);

            widget.addWidget(bar);
        }

        element(widget);

        for (HistElement elem : elems) {
            histProperty(elem);
        }

        return this;
    }

    public void histProperty(HistElement elem) {
        Widget widget = new Widget(expectWidth - 10, 8).pos(6, 0);

        Widget keyArea = new Widget()
                .pos(4, 0)
                .size(32, 8)
                .halign(Transform.HeightAlign.CENTER)
                .addComponent((newTextBox(new IFont.FontOption(8)).setContent(elem.getId())));

        Widget icon = new Widget()
                .size(6, 6)
                .halign(Transform.HeightAlign.CENTER)
                .pos(-3, 0.5f)
                .addComponents(new DrawTexture(null).setColor(elem.getColor()));

        int keyLength = 40;
        Widget valueArea = new Widget()
                .pos(keyLength, 0)
                .size(40, 8)
                .halign(Transform.HeightAlign.CENTER)
                .addComponent(newTextBox(new IFont.FontOption(8)).setContent(elem.getDesc()));

        valueArea.listen(FrameEvent.class, () -> {
            TextBox textBox = valueArea.getComponent(TextBox.class);
            if (textBox != null) {
                textBox.setContent(elem.getDesc());
            }
        });

        widget.addWidget(keyArea);
        widget.addWidget(icon);
        widget.addWidget(valueArea);

        element(widget);
    }

    // will rewrite
    public InfoArea sepline(String id) {
/*        Widget widget = new Widget(expectWidth - 3, 8).pos(3, 0);
        widget.addComponent(new DrawTexture());
        elemY += 30;
        element(widget);*/
        return this;
    }

    public InfoArea element(Widget elem) {
        elem.transform.y = expectHeight + 2;

        int elemHeight = 5;
        expectHeight = expectHeight + elem.transform.height * elem.transform.scale + elemHeight;

        this.addWidget(elem);

        return this;
    }

    public static void drawTextBox(String content, IFont.FontOption option, float x, float y, float limit) {
        int wmargin = 5;
        int hmargin = 2;

        IFont font = Resources.font();
        IFont.Extent extent = font.drawSeperated_Sim(content, limit, option);

        GL11.glColor4f(0, 0, 0, 0.5f);
        HudUtils.colorRect(x - extent.width * option.align.lenOffset, y, extent.width + wmargin * 2 + 2, extent.height + hmargin * 2);

        GL11.glColor4f(1, 1, 1, 0.8f);
        font.drawSeperated(content, x + wmargin, y + hmargin, limit, option);

        GL11.glColor4f(1, 1, 1, 1);
    }

    public class BlendQuad extends Component {
        private Color color = Colors.monoBlend(0.0f, 0.5f);

        public BlendQuad() {
            super("BlendQuad");
            this.listen(FrameEvent.class, (w, event) -> {
                RenderUtils.loadTexture(blendQuadTex);
                Colors.bindToGL(color);

                // Helper method to draw a quad
                drawQuad();

                glColor4d(1, 1, 1, 1);
                RenderUtils.loadTexture(lineTex);

                double mrg = 3.2;
                HudUtils.rect(-mrg, -8.6, widget.transform.width + mrg * 2, 12);
                HudUtils.rect(-mrg, widget.transform.height - 2, widget.transform.width + mrg * 2, 8);
            });
        }

        private void drawQuad() {
            double x = 0, y = 0, w = widget.transform.width, h = widget.transform.height;
            double margin = 4;
            double[] xs = {x - margin, x, x + w, x + w + margin};
            double[] ys = {y - margin, y, y + h, y + h + margin};

            GL11.glBegin(GL11.GL_QUADS);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    drawSingleQuad(i, j, xs[i], ys[j], xs[i + 1], ys[j + 1]);
                }
            }
            GL11.glEnd();
        }

        private void drawSingleQuad(int col, int row, double x0, double y0, double x1, double y1) {
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
    }
}
