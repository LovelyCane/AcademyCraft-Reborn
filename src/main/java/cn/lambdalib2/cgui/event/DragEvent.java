/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;

/**
 * Solely a notification event, fired on current focus when it was dragged.
 * 
 * @author WeAthFolD
 */
public class DragEvent implements GuiEvent {
    /**
     * Offset coordinates from dragging widget origin to current mouse position, in global scale level.
     */
    public final float offsetX, offsetY;

    public DragEvent(float _offsetX, float _offsetY) {
        offsetX = _offsetX;
        offsetY = _offsetY;
    }
}
