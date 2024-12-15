/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.event;

/**
 * Fired on CGui and current widget focus, when player presses any key.
 */
public class KeyEvent implements GuiEvent {
    public final char inputChar;
    public final int keyCode;
    
    public KeyEvent(char _ch, int _key) {
        inputChar = _ch;
        keyCode = _key;
    }
}
