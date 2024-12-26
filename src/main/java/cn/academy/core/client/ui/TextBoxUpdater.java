package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.util.Colors;

import java.util.ArrayList;
import java.util.List;

public class TextBoxUpdater implements Updater<TextBox> {
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
