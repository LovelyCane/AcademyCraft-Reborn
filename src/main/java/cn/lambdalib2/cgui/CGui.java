/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.cgui;

import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.cgui.event.*;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.render.font.IFont.FontOption;
import cn.lambdalib2.render.font.TrueTypeFont;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.HudUtils;
import cn.lambdalib2.util.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.util.Iterator;

/**
 * @author WeathFolD
 */

public class CGui extends WidgetContainer {
    static final double DRAG_TIME_TOLE = 0.1;

    private float width, height; //Only useful when calculating 'CENTER' align preference

    //Absolute mouse position.
    private float mouseX, mouseY;

    Widget focus; //last input focus

    private final GuiEventBus eventBus = new GuiEventBus();

    private double lastStartTime, lastDragTime;
    private Widget draggingNode;
    private float xOffset, yOffset;

    private boolean debug;

    private double lastFrameTime = -1;

    public float getDeltaTime() {
        return (float) deltaTime;
    }

    private double deltaTime = 0;

    public CGui() {
    }

    public CGui(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void dispose() {
    }

    /**
     * Called when screen is being resized.
     *
     * @param w new width
     * @param h new height
     */
    public void resize(float w, float h) {
        boolean diff = width != w || height != h;

        this.width = w;
        this.height = h;

        if (diff) {
            for (Widget widget : this) {
                widget.dirty = true;
            }
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setDebug() {
        debug = true;
    }

    //---Event callback---

    /**
     * Simplified version of {@link #draw(float mx, float my)} callback.
     */
    public void draw() {
        draw(-1, -1);
    }

    /**
     * Go down the hierarchy tree and draw each widget node. Should be called each rendering frame.
     */
    public void draw(float mx, float my) {
        frameUpdate();
        updateMouse(mx, my);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTraverse(mx, my, null, this, getTopWidget(mx, my));

        if (debug) {
            Widget hovering = getHoveringWidget();
            if (hovering != null) {
                GL11.glColor4f(1, .5f, .5f, .8f);
                HudUtils.drawRectOutline(hovering.x, hovering.y,
                        hovering.transform.width * hovering.scale,
                        hovering.transform.height * hovering.scale, 3);
                IFont font = TrueTypeFont.defaultFont;
                font.draw(hovering.getFullName(), hovering.x, hovering.y - 10, new FontOption(10));
            }

        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    @Override
    public boolean addWidget(String name, Widget w) {
        if (this.hasWidget(name))
            return false;
        super.addWidget(name, w);
        eventBus.postEvent(null, new AddWidgetEvent(w));
        return true;
    }

    /**
     * Standard GuiScreen class callback.
     *
     * @param mx
     * @param my
     * @param btn the mouse button ID.
     * @param dt  how long is this button being pressed(ms)
     */
    public boolean mouseClickMove(int mx, int my, int btn, long dt) {
        updateMouse(mx, my);
        if (btn == 0) {
            double time = GameTimer.getAbsTime();
            if (draggingNode == null) {
                lastStartTime = time;
                draggingNode = getTopWidget(mx, my);
                //    System.out.println("StartDragging " + draggingNode);
                if (draggingNode == null)
                    return false;
                xOffset = mx - draggingNode.x;
                yOffset = my - draggingNode.y;
            }
            lastDragTime = time;
            draggingNode.post(new DragEvent(xOffset, yOffset));
            return true;
        }
        return false;
    }

    private void postMouseEv(Widget target, GuiEventBus bus, int mx, int my, int bid, boolean local) {
        float x = mx, y = my;
        if (local) {
            x = (mx - target.x) / target.scale;
            y = (my - target.y) / target.scale;
        }

        bus.postEvent(target, new MouseClickEvent(x, y, bid));
        if (bid == 0)
            bus.postEvent(target, new LeftClickEvent(x, y));
        if (bid == 1)
            bus.postEvent(target, new RightClickEvent(x, y));
    }

    /**
     * Standard GuiScreen mouseClicked callback.
     *
     * @return if any action was performed on a widget.
     */
    public boolean mouseClicked(int mx, int my, int bid) {
        updateMouse(mx, my);

        postMouseEv(null, eventBus, mx, my, bid, false);

        Widget top = getTopWidget(mx, my);
        if (top != null) {
            if (bid == 0) {
                gainFocus(top);
            } else {
                removeFocus();
            }
            postMouseEv(top, top.eventBus(), mx, my, bid, true);
            return true;
        }
        return false;
    }

    public void removeFocus() {
        removeFocus(null);
    }

    public void removeFocus(Widget newFocus) {
        if (focus != null) {
            focus.post(new LostFocusEvent(newFocus));
            focus = null;
        }
    }

    /**
     * Gain a widget's focus with force.
     */
    public void gainFocus(Widget node) {
        if (node == focus) {
            return;
        }
        //  Debug.log("GainFocus " + node);
        if (focus != null) {
            removeFocus(node);
        }
        focus = node;
        focus.post(new GainFocusEvent());
    }

    public void keyTyped(char ch, int key) {
        if (focus != null) {
            focus.post(new KeyEvent(ch, key));
        }
    }

    //---Helper Methods---
    public Widget getTopWidget(float x, float y) {
        return gtnTraverse(x, y, null, this);
    }

    public Widget getHoveringWidget() {
        return getTopWidget(mouseX, mouseY);
    }

    public void updateDragWidget() {
        if (draggingNode != null) {
            moveWidgetToAbsPos(draggingNode, mouseX - xOffset, mouseY - yOffset);
        }
    }

    /**
     * Inverse calculation. Move this widget to the ABSOLUTE window position (x0, y0).
     */
    public void moveWidgetToAbsPos(Widget widget, float x0, float y0) {
        Transform transform = widget.transform;

        float tx, ty;
        float tw, th;
        float parentScale;
        if (widget.isWidgetParent()) {
            Widget p = widget.getWidgetParent();
            tx = p.x;
            ty = p.y;
            tw = p.transform.width * p.scale;
            th = p.transform.height * p.scale;
            parentScale = p.scale;
        } else {
            tx = ty = 0;
            tw = width;
            th = height;

            parentScale = 1;
        }

        transform.x = (x0 - tx - transform.alignWidth.factor * (tw - transform.width * widget.scale)) / parentScale;
        transform.y = (y0 - ty - transform.alignHeight.factor * (th - transform.height * widget.scale)) / parentScale;

        widget.x = x0;
        widget.y = y0;

        widget.dirty = true;
    }

    public Widget getDraggingWidget() {
        return Math.abs(GameTimer.getAbsTime() - lastDragTime) > DRAG_TIME_TOLE ||
                draggingNode == null ? null : draggingNode;
    }

    public Widget getFocus() {
        return focus;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    //---Key Handling

    //---Internal Processing
    public void updateWidget(Widget widget) {
        // Because Widgets can have sub widgets before they are added into CGui,
        // we manually assign the gui per update process.
        // It's up to user to not update the widget in the wrong CGui instance.
        widget.gui = this;
        Transform transform = widget.transform;

        float tx, ty;
        float tw, th;
        float parentScale;
        if (widget.isWidgetParent()) {
            Widget p = widget.getWidgetParent();
            tx = p.x;
            ty = p.y;
            tw = p.transform.width * p.scale;
            th = p.transform.height * p.scale;
            parentScale = p.scale;

            widget.scale = transform.scale * p.scale;
        } else {
            tx = ty = 0;
            tw = width;
            th = height;

            parentScale = 1;
            widget.scale = transform.scale;
        }

        widget.x = tx +
                (tw - transform.width * widget.scale) * transform.alignWidth.factor +
                transform.x * parentScale;

        widget.y = ty +
                (th - transform.height * widget.scale) * transform.alignHeight.factor +
                transform.y * parentScale;

        widget.dirty = false;

        //Check sub widgets
        for (Widget w : widget) {
            updateWidget(w);
        }
    }

    /**
     * Generic checking.
     */
    private void frameUpdate() {
        double time = GameTimer.getAbsTime();

        if (lastFrameTime == -1)
            deltaTime = 0;
        else
            deltaTime = MathUtils.clampd(0f, 0.1f, GameTimer.getAbsTime() - lastFrameTime);

        lastFrameTime = time;

        // Update drag
        if (draggingNode != null) {
            if (time - lastDragTime > DRAG_TIME_TOLE) {
                draggingNode.post(new DragStopEvent());
                draggingNode = null;
            }
        }
        //

        updateTraverse(null, this);
        this.update();
    }

    private void updateTraverse(Widget cur, WidgetContainer set) {
        if (cur != null) {
            if (cur.dirty) {
                cur.post(new RefreshEvent());
                this.updateWidget(cur);
            }
        }

        Iterator<Widget> iter = set.iterator();
        while (iter.hasNext()) {
            Widget widget = iter.next();
            if (!widget.disposed) {
                updateTraverse(widget, widget);
                widget.update();
            }
        }
    }

    private void updateMouse(float mx, float my) {
        this.mouseX = mx;
        this.mouseY = my;
    }

    private void drawTraverse(float mx, float my, Widget cur, WidgetContainer set, Widget top) {
        try {
            if (cur != null && cur.isVisible()) {
                GL11.glPushMatrix();
                GL11.glTranslated(cur.x, cur.y, 0);

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glScaled(cur.scale, cur.scale, 1);
                GL11.glTranslated(-cur.transform.pivotX, -cur.transform.pivotY, 0);

                GL11.glColor4d(1, 1, 1, 1); //Force restore color for any widget
                cur.post(new FrameEvent((mx - cur.x) / cur.scale, (my - cur.y) / cur.scale, cur == top, (float) deltaTime));
                GL11.glPopMatrix();

                int error = GL11.glGetError();
                if (error != GL11.GL_NO_ERROR) {
                    Debug.error("[CGUI] Error while rendering " + cur.getFullName() + ": " + error + "/" + GLU.gluErrorString(error));
                }
            }
        } catch (Exception e) {
            Debug.error("Error occured handling widget draw. instance class: " + cur.getClass().getName() + ", name: " + cur.getName());
            e.printStackTrace();
        }

        if (cur == null || cur.isVisible()) {
            Iterator<Widget> iter = set.iterator();
            while (iter.hasNext()) {
                Widget wn = iter.next();
                drawTraverse(mx, my, wn, wn, top);
            }
        }
    }

    protected Widget gtnTraverse(float x, float y, Widget node, WidgetContainer set) {
        Widget res = null;
        boolean checkSub = node == null || node.isVisible();
        if (node != null && node.isVisible()
                && node.transform.doesListenKey
                && node.isPointWithin(x, y)) {
            res = node;
        }

        if (!checkSub) return res;

        Widget next = null;
        for (Widget wn : set) {
            Widget tmp = gtnTraverse(x, y, wn, wn);
            if (tmp != null)
                next = tmp;
        }
        return next == null ? res : next;
    }

    @Override
    protected void onWidgetAdded(String name, Widget w) {
        w.gui = this;
        updateWidget(w);
    }

    public <T extends GuiEvent> void listen(Class<? extends T> clazz, IGuiEventHandler<T> handler) {
        eventBus.listen(clazz, handler, 0);
    }

    public <T extends GuiEvent> void unlisten(Class<? extends T> clazz, IGuiEventHandler<T> handler) {
        eventBus.unlisten(clazz, handler);
    }

    public void postEvent(GuiEvent event) {
        eventBus.postEvent(null, event);
    }

    /**
     * Event bus delegator, will post every widget inside this CGui. <br>
     * Note that this might impact peformance when used incorectlly.
     */
    public void postEventHierarchically(GuiEvent event) {
        eventBus.postEvent(null, event);
        for (Widget w : getDrawList()) {
            hierPostEvent(w, event);
        }
    }

    private void hierPostEvent(Widget w, GuiEvent event) {
        w.post(event);
        for (Widget ww : w.widgetList) {
            hierPostEvent(ww, event);
        }
    }
}
