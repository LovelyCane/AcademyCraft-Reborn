/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;

import java.util.*;
import java.util.Map.Entry;

import cn.lambdalib2.cgui.Widget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiEventBus {
    private static class NodeCollection extends LinkedList<GuiHandlerNode> {
        
        List<GuiHandlerNode> toadd = new ArrayList<>();
        List<Object> toremove = new ArrayList<>();
        
        boolean iterating;
        
        @Override
        public boolean add(GuiHandlerNode node) {
            if(iterating) {
                toadd.add(node);
                return true;
            } else {
                boolean res = super.add(node);
                if(res) {
                    this.sort(priorityCmp);
                }
                return res;
            }
        }
        
        @Override
        public boolean remove(Object o) {
            if(iterating) {
                toremove.add(o);
                return true;
            } else {
                return super.remove(o);
            }
        }
        
        void startIterating() { iterating = true; }
        
        void endIterating() {
            iterating = false;
            addAll(toadd);
            removeAll(toremove);
            if(!toadd.isEmpty()) {
                this.sort(priorityCmp);
            }
            toadd.clear();
            toremove.clear();
        }
        
    }
    
    public GuiEventBus() {}
    
    Map< Class<? extends GuiEvent>, NodeCollection > eventHandlers = new HashMap<>();
    
    public void postEvent(Widget widget, GuiEvent event) {
        NodeCollection list = eventHandlers.get(event.getClass());
        if(list != null) {
            list.startIterating();
            for(GuiHandlerNode n : list) {
                n.handler.handleEvent(widget, event);
            }
            list.endIterating();
        }
    }
    
    public <T extends GuiEvent> void listen(Class<? extends T> clazz, IGuiEventHandler<T> handler) {
        listen(clazz, handler, 0);
    }
    
    public <T extends GuiEvent> void listen(Class<? extends T> clazz, IGuiEventHandler<T> handler, int priority) {
        listen(clazz, handler, priority, true);
    }
    
    public <T extends GuiEvent> void listen(Class<? extends T> clazz, IGuiEventHandler<T> handler, int priority, boolean copyable) {
        NodeCollection list = getRawList(clazz);
        // Perform gracefully if handler is duplicated. This allows safe copy.
        for(GuiHandlerNode n : list) {
            if(n.handler == handler)
                return;
        }
        list.add(new GuiHandlerNode(handler, priority, copyable));
    }

    /**
     * Remove the given listener from this event bus, if it is in the bus. This approach is slightly faster.
     */
    public <T extends GuiEvent> void unlisten(Class<? extends GuiEvent> clazz, IGuiEventHandler<T> handler) {
        getRawList(clazz).remove(new GuiHandlerNode(handler, 0, false));
    }

    private NodeCollection getRawList(Class<? extends GuiEvent> clazz) {
        NodeCollection ret = eventHandlers.get(clazz);
        if(ret == null) {
            eventHandlers.put(clazz, ret = new NodeCollection());
        }
        
        return ret;
    }
    
    /**
     * Copies (or clones) the event bus. 
     * The cloned event bus will retain all copyable event handlers of the previous bus, as reference.
     */
    public GuiEventBus copy() {
        GuiEventBus ret = new GuiEventBus();
        
        for(Entry< Class<? extends GuiEvent>, NodeCollection> ent : eventHandlers.entrySet()) {
            NodeCollection list = ret.getRawList(ent.getKey());
            for(GuiHandlerNode n : ent.getValue()) {
                if(n.copySensitive) {
                    list.add(n);
                }
            }
        }
        return ret;
    }
    
    private static class GuiHandlerNode {
        final IGuiEventHandler handler;
        final int priority;
        final boolean copySensitive;

        public GuiHandlerNode(IGuiEventHandler _handler, int _priority, boolean _copySensitive) {
            handler = _handler;
            priority = _priority;
            copySensitive = _copySensitive;
        }
        
        @Override
        public boolean equals(Object another) {
            return another instanceof GuiHandlerNode && ((GuiHandlerNode) another).handler == handler;
        }
        
        @Override
        public int hashCode() {
            return handler.hashCode();
        }
    }

    static final Comparator<GuiHandlerNode> priorityCmp = Comparator.comparingInt(n -> -n.priority);
}
