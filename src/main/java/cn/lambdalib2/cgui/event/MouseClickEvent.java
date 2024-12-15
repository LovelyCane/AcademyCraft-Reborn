/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;

/**
 * Fired on both CGui and current focus when any mouse button except for LMB and RMB is clicked.
 * (For convenience reasons, they are handled in {@link LeftClickEvent} and {@link RightClickEvent}.)
 */
public class MouseClickEvent implements GuiEvent {

    /**
     * Mouse position in local coordinate space.
     */
    public final double mx, my;
    
    /**
     * Pressed button id.
     */
    public final int button;
    
    public MouseClickEvent(double _mx, double _my, int bid) {
        mx = _mx;
        my = _my;
        button = bid;
    }

}
