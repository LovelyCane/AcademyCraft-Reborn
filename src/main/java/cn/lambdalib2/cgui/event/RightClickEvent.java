/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;

/**
 * Fired on both CGui and current focus when user presses right mouse button.
 */
public class RightClickEvent implements GuiEvent {

    public final double x, y;
    
    public RightClickEvent(double _x, double _y) {
        x = _x;
        y = _y;
    }

}
