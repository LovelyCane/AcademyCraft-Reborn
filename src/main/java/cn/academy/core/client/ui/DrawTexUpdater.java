package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.util.Colors;

import java.util.ArrayList;
import java.util.List;

public class DrawTexUpdater implements Updater<DrawTexture> {
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

