package cn.academy.core.client.ui;

import cn.lambdalib2.util.Colors;
import org.lwjgl.util.Color;

import static cn.academy.core.client.ui.ContainerUI.localHist;

public class HistUtils {
    private HistUtils() {
    }

    public static HistElement histEnergy(double energy, double max) {
        Color color = Colors.fromHexColor(0xff25c4ff);
        return new HistElement(
                localHist.get("energy"),
                color,
                energy / max,
                String.format("%.0f IF", energy)
        );
    }

    public static HistElement histBuffer(double energy, double max) {
        Color color = Colors.fromHexColor(0xff25f7ff);
        return new HistElement(
                localHist.get("buffer"),
                color,
                energy / max,
                String.format("%.0f IF", energy)
        );
    }

    public static HistElement histPhaseLiquid(double amt, double max) {
        Color color = Colors.fromHexColor(0xff7680de);
        return new HistElement(
                localHist.get("liquid"),
                color,
                amt / max,
                String.format("%.0f mB", amt)
        );
    }

    public static HistElement histCapacity(int amt, int max) {
        Color color = Colors.fromHexColor(0xffff6c00);
        return new HistElement(
                localHist.get("capacity"),
                color,
                (double) amt / max,
                amt + "/" + max
        );
    }
}
