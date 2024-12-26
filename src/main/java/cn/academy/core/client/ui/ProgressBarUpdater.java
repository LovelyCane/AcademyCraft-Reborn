package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.util.Colors;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarUpdater implements Updater<ProgressBar> {
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