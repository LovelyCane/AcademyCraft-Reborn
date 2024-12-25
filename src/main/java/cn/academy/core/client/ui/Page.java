package cn.academy.core.client.ui;

import cn.lambdalib2.cgui.Widget;

public class Page {
    public final String id;
    public final Widget window;

    public Page(String id, Widget window) {
        this.id = id;
        this.window = window;
    }

    public String getId() {
        return id;
    }

    public Widget getWindow() {
        return window;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", window=" + window +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id.equals(page.id) && window.equals(page.window);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + window.hashCode();
        return result;
    }
}
