/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;


/**
 * Fired on CGui and current focus when user presses left mouse button.
 */
public class LeftClickEvent implements GuiEvent {
    public final float x, y;
    
    public LeftClickEvent(float _x, float _y) {
        x = _x;
        y = _y;
    }
}
