package cn.academy.core.client.ui;

import cn.academy.Resources;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;

import java.util.Objects;
import java.util.function.Supplier;

public class HistElement {
    public static final ResourceLocation histogramTex = Resources.getTexture("guis/histogram");
    public final String name;
    public final Color color;
    public final Supplier<Double> value;
    public final Supplier<String> desc;

    public HistElement(String name, Color color, Supplier<Double> value, Supplier<String> desc) {
        this.name = name;
        this.color = color;
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "HistElement{" +
                "id='" + name + '\'' +
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
        return Objects.equals(name, that.name) &&
                Objects.equals(color, that.color) &&
                Objects.equals(value, that.value) &&
                Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, value, desc);
    }
}
