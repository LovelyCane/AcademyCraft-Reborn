package cn.academy.core.client.ui;

import cn.academy.Resources;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;

import java.util.Objects;

public class HistElement {
    public static final ResourceLocation histogramTex = Resources.getTexture("guis/histogram");
    public final String id;
    public final Color color;
    public final double value;
    public final String desc;

    public HistElement(String id, Color color, double value, String desc) {
        this.id = id;
        this.color = color;
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "HistElement{" +
                "id='" + id + '\'' +
                ", color=" + color +
                ", value=" + value +
                ", desc=" + desc +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistElement that = (HistElement) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(color, that.color) &&
                Objects.equals(value, that.value) &&
                Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, color, value, desc);
    }
}
