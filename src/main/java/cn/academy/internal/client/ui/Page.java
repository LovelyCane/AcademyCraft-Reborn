package cn.academy.internal.client.ui;

import cn.lambdalib2.cgui.Widget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class Page {
    public final String id;
    public final Widget window;

    public Page(String id, Widget window) {
        this.id = id;
        this.window = window;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(id, page.id) && Objects.equals(window, page.window);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, window);
    }

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", window=" + window +
                '}';
    }
}
