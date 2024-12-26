/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.cgui.component;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.event.GuiEvent;
import cn.lambdalib2.cgui.event.IGuiEventHandler;
import cn.lambdalib2.s11n.CopyHelper;
import cn.lambdalib2.s11n.SerializeExcluded;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * <summary>
 * Component is attached to Widget. It can define a set of EventHandlers and store information by itself.
 * </summary>
 * <p>
 * Components supports prototype patteren natively. They can be copied to make duplicates, typically when its
 * container widget is being copied.
 * </p>
 *
 * @author WeAthFolD
 */

@SideOnly(Side.CLIENT)
public class Component {
    public final String name;

    public boolean enabled = true;

    public boolean canEdit = true;

    /**
     * The widget that this component is attached to. To ease impl and usage, this is exposed as
     * public field, but DONT assign it, else it yields undefined behaviour.
     */
    @SerializeExcluded
    public Widget widget;

    public Component(String _name) {
        name = _name;
    }

    public <T extends GuiEvent> void listen(Class<? extends T> type, IGuiEventHandler<T> handler) {
        listen(type, handler, 0);
    }

    public <T extends GuiEvent> void listen(Class<? extends T> type, IGuiEventHandler<T> handler, int prio) {
        if (widget != null)
            throw new RuntimeException("Can only add event handlers before componenet is added into widget");
        Node n = new Node();
        n.type = type;
        n.handler = new EHWrapper<>(handler);
        n.prio = prio;
        addedHandlers.add(n);
    }

    /**
     * Called when the component is added into a widget, and the widget field is correctly set.
     */
    public void onAdded() {
        for (Node n : addedHandlers) {
            widget.listen(n.type, n.prio, false, n.handler);
        }
    }

    public void onRemoved() {
        for (Node n : addedHandlers) {
            widget.unlisten(n.type, n.handler);
        }
    }

    public Component copy() {
        return CopyHelper.instance.copy(this);
    }

    private final List<Node> addedHandlers = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    private final class EHWrapper<T extends GuiEvent> implements IGuiEventHandler<T> {

        final IGuiEventHandler<T> wrapped;

        public EHWrapper(IGuiEventHandler<T> _wrapped) {
            wrapped = _wrapped;
        }

        @Override
        public void handleEvent(Widget w, T event) {
            if (enabled) wrapped.handleEvent(w, event);
        }

    }

    @SideOnly(Side.CLIENT)
    private static class Node {
        Class<? extends GuiEvent> type;
        IGuiEventHandler handler;
        int prio;
    }
}

