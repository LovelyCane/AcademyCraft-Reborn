package cn.academy.core.client.ui;

import cn.academy.util.LocalHelper;
import org.lwjgl.util.Color;

import static cn.lambdalib2.util.Colors.fromHexColor;

public class HistElement {
    private final String id;
    private final Color color;
    private final double value;
    private final String desc;
    static LocalHelper local = LocalHelper.at("ac.gui.common");
    public static LocalHelper localHist = local.subPath("hist");

    public HistElement(String id, Color color, double value, String desc) {
        this.id = id;
        this.color = color;
        this.value = value;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public double getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static HistElement histEnergy(double energy, double max) {
        Color color = fromHexColor(0xff25c4ff);
        return new HistElement(localHist.get("energy"), color, energy / max, String.format("%.0f IF", energy));
    }

    public static HistElement histBuffer(double energy, double max) {
        Color color = fromHexColor(0xff25f7ff);
        return new HistElement(localHist.get("buffer"), color,  energy / max,String.format("%.0f IF", energy));
    }

    public static HistElement histPhaseLiquid(double amt, double max) {
        Color color = fromHexColor(0xff7680de);
        return new HistElement(localHist.get("liquid"), color,amt / max,String.format("%.0f mB", amt));
    }

    public static HistElement histCapacity(int amt, double max) {
        Color color = fromHexColor(0xffff6c00);
        return new HistElement(localHist.get("capacity"), color, (double) amt / max,String.format("%d/%f", amt, max));
    }
}
