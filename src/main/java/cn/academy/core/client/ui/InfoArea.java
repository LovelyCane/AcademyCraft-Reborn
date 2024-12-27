package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.MathUtils;
import org.lwjgl.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static cn.academy.Resources.newTextBox;
import static cn.academy.core.client.ui.ContainerUI.localProperty;
import static cn.academy.core.client.ui.ContainerUI.localSep;
import static cn.lambdalib2.util.MathUtils.lerp;

public class InfoArea extends Widget {
    private final DrawTexUpdater drawTexUpdater = new DrawTexUpdater();
    private final TextBoxUpdater textBoxUpdater = new TextBoxUpdater();
    private final ProgressBarUpdater progressBarUpdater = new ProgressBarUpdater();

    private final double startTime = GameTimer.getTime();

    private final float expectWidth = 100f;
    private float expectHeight = 50f;

    private final int keyLength = 40;
    private float elemY = 10f;

    private final List<Widget> elements = new ArrayList<>();
    private final List<Updater<?>> uas = Arrays.asList(drawTexUpdater, textBoxUpdater, progressBarUpdater);

    private static final double alphaWidthDuration = 10;
    private static final double alphaHeightDuration = 5;
    private static final double balphaDuration = 0.3;

    public InfoArea() {
        this.addComponent(new BlendQuad());

        this.listen(FrameEvent.class, () -> {
            double time = GameTimer.getTime();

            double alphaWidth = Math.max(0, Math.min(1, (time - startTime) / alphaWidthDuration));

            double alphaHeight = Math.max(0, Math.min(1, (time - startTime) / alphaHeightDuration));

            transform.width = (float) lerp(transform.width, expectWidth, alphaWidth);
            transform.height = (float) lerp(transform.height, expectHeight, alphaHeight);

            double balpha = Math.max(0, Math.min(1, (time - startTime - 0.3) / balphaDuration));

            for (Updater<?> ua : uas) {
                ua.apply((float) balpha);
            }
        });
    }

    public InfoArea reset() {
        for (Widget elem : elements) {
            elem.dispose();
        }
        elements.clear();
        elemY = 10f;
        for (Updater<?> ua : uas) {
            ua.clear();
        }
        return this;
    }

    public InfoArea button(String name, Runnable callback) {
        TextBox textBox = newTextBox(new IFont.FontOption(9, IFont.FontAlign.CENTER)).setContent(name);
        textBoxUpdater.add(textBox);
        float len = textBox.font.getTextWidth(name, textBox.option);
        Widget widget = new Widget().walign(Transform.WidthAlign.CENTER).size(Math.max(50, len + 5), 8);
        widget.listen(FrameEvent.class, (w, event) -> {
            float lum = event.hovering ? 1.0f : 0.8f;
            Color color = textBox.option.color;
            color.setRed(Colors.f2i(lum));
            color.setGreen(Colors.f2i(lum));
            color.setBlue(Colors.f2i(lum));
        });
        widget.listen(LeftClickEvent.class, callback);
        widget.addComponent(textBox);
        element(widget);
        return this;
    }

    public InfoArea histogram(HistElement... elems) {
        DrawTexture backgroundTexture = new DrawTexture(HistElement.histogramTex);
        drawTexUpdater.add(backgroundTexture);
        Widget widget = new Widget().size(210, 210).scale(0.4f).addComponent(backgroundTexture);
        for (int idx = 0; idx < elems.length; idx++) {
            HistElement elem = elems[idx];
            Widget bar = new Widget().size(16, 120).pos(56 + idx * 40, 78);
            ProgressBar progress = new ProgressBar();
            progressBarUpdater.add(progress);
            progress.color = elem.color;
            progress.setDirection(ProgressBar.Direction.UP);
            bar.listen(FrameEvent.class, () -> progress.progress = MathUtils.clampd(0.03, 1, elem.value.get()));
            bar.addComponent(progress);
            widget.addWidget(bar);
        }
        blank(-30);
        element(widget);
        for (HistElement elem : elems) {
            histProperty(elem);
        }
        return this;
    }

    public InfoArea element(Widget elem) {
        elem.transform.y = elemY;
        elemY += elem.transform.height * elem.transform.scale;
        elements.add(elem);
        expectHeight = Math.max(50.0f, elemY + 8);
        this.addWidget(elem);
        return this;
    }

    private void histProperty(HistElement elem) {
        Widget widget = new Widget(expectWidth - 10, 8).pos(6, 0);
        TextBox keyAreaTextBox = newTextBox(new IFont.FontOption(8));
        textBoxUpdater.add(keyAreaTextBox);
        Widget keyArea = new Widget().pos(4, 0).size(32, 8).halign(Transform.HeightAlign.CENTER).addComponent(keyAreaTextBox.setContent(elem.name));
        DrawTexture iconDrawTexture = new DrawTexture(null).setColor(elem.color);
        drawTexUpdater.add(iconDrawTexture);
        Widget icon = new Widget().size(6, 6).halign(Transform.HeightAlign.CENTER).pos(-3, 0.5f).addComponents(iconDrawTexture);
        TextBox iconAreaTextBox = newTextBox(new IFont.FontOption(8));
        textBoxUpdater.add(iconAreaTextBox);
        Widget valueArea = new Widget().pos(keyLength, 0).size(40, 8).halign(Transform.HeightAlign.CENTER).addComponent(iconAreaTextBox.setContent(elem.name));
        valueArea.listen(FrameEvent.class, () -> valueArea.getComponent(TextBox.class).setContent(elem.desc.get()));
        widget.addWidget(keyArea);
        widget.addWidget(icon);
        widget.addWidget(valueArea);
        element(widget);
    }

    public InfoArea blank(double ht) {
        elemY += (float) ht;
        return this;
    }

    public InfoArea sepline(String id) {
        Widget widget = new Widget(expectWidth - 3, 8).pos(3, 0);
        TextBox widgetTextBox = newTextBox(new IFont.FontOption(6, Colors.monoBlend(1, 0.6f))).setContent(localSep.get(id));
        textBoxUpdater.add(widgetTextBox);
        widget.addComponent(widgetTextBox);
        blank(3);
        element(widget);
        return this;
    }

    public InfoArea property(String key, Object value, Function<String, ?> editCallback, boolean password, boolean colorChange, TextBox[] contentCell) {
        final Color idleColor = Colors.fromHexColor(0xffffffff);
        final Color editColor = Colors.fromHexColor(0xff2180d8);
        TextBox textBox = newTextBox(new IFont.FontOption(8)).setContent(value.toString());
        textBoxUpdater.add(textBox);
        Widget valueArea = new Widget().size(40, 8).halign(Transform.HeightAlign.CENTER);
        if (editCallback != null) {
            textBox.allowEdit = true;
            textBox.option.color.setColor(idleColor);
            valueArea.listen(TextBox.ConfirmInputEvent.class, () -> {
                if (colorChange) {
                    textBox.option.color.setColor(idleColor);
                }
                editCallback.apply(textBox.content);
            });
            valueArea.listen(TextBox.ChangeContentEvent.class, () -> {
                if (colorChange) {
                    textBox.option.color.setColor(editColor);
                }
            });
            Widget box0 = box("[").pos(-4, 0);
            Widget box1 = box("]").pos(valueArea.transform.width + 2, 0);
            box0.transform.doesListenKey = false;
            box1.transform.doesListenKey = false;
            valueArea.addWidget(box0);
            valueArea.addWidget(box1);
        } else {
            valueArea.listen(FrameEvent.class, () -> textBox.setContent(value.toString()));
        }
        if (password) {
            textBox.doesEcho = true;
        }
        valueArea.addComponent(textBox);
        kvpair(key, valueArea);
        if (contentCell != null) {
            contentCell[0] = textBox;
        }
        return this;
    }

    private void kvpair(String key, Widget value) {
        Widget widget = new Widget(expectWidth - 10, 8).pos(6, 0);
        TextBox keyAreaTextBox = newTextBox(new IFont.FontOption(8)).setContent(localProperty.get(key));
        textBoxUpdater.add(keyAreaTextBox);
        Widget keyArea = new Widget().size(40, 8).halign(Transform.HeightAlign.CENTER).addComponent(keyAreaTextBox);
        value.pos(keyLength, 0);
        widget.addWidget(keyArea);
        widget.addWidget(value);
        element(widget);
    }

    Widget box(String ch) {
        TextBox textBox = Resources.newTextBox(new IFont.FontOption(8)).setContent(ch);
        textBoxUpdater.add(textBox);
        return new Widget().size(10, 8).halign(Transform.HeightAlign.CENTER).addComponent(textBox);
    }
}

interface Updater<T> {
    void add(T obj);

    void clear();

    void apply(float alpha);

    void apply(T obj, float alpha);
}

class DrawTexUpdater implements Updater<DrawTexture> {
    private final List<DrawTexture> us = new ArrayList<>();

    @Override
    public void add(DrawTexture obj) {
        us.add(obj);
    }

    @Override
    public void clear() {
        us.clear();
    }

    @Override
    public void apply(float alpha) {
        for (DrawTexture obj : us) {
            apply(obj, alpha);
        }
    }

    @Override
    public void apply(DrawTexture obj, float alpha) {
        obj.color.setAlpha(Colors.f2i(alpha));
    }
}

class ProgressBarUpdater implements Updater<ProgressBar> {
    private final List<ProgressBar> us = new ArrayList<>();

    @Override
    public void add(ProgressBar obj) {
        us.add(obj);
    }

    @Override
    public void clear() {
        us.clear();
    }

    @Override
    public void apply(float alpha) {
        for (ProgressBar obj : us) {
            apply(obj, alpha);
        }
    }

    @Override
    public void apply(ProgressBar obj, float alpha) {
        obj.color.setAlpha(Colors.f2i(alpha));
    }
}

class TextBoxUpdater implements Updater<TextBox> {
    private final List<TextBox> us = new ArrayList<>();

    @Override
    public void add(TextBox obj) {
        us.add(obj);
    }

    @Override
    public void clear() {
        us.clear();
    }

    @Override
    public void apply(float alpha) {
        for (TextBox obj : us) {
            apply(obj, alpha);
        }
    }

    @Override
    public void apply(TextBox obj, float alpha) {
        obj.option.color.setAlpha(Colors.f2i(alpha));
    }
}
