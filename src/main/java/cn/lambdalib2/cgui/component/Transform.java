/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.component;

/**
 * Transform is the base component of a widget. It cannot be removed. It provides some meta-information such as widget align and placement.
 * @author WeAthFolD
 */
public class Transform extends Component {
    public enum WidthAlign { LEFT, CENTER, RIGHT;
        public final float factor;
        WidthAlign() {
            factor = ordinal() * 0.5f;
        }
    }
    
    public enum HeightAlign { TOP, CENTER, BOTTOM;
        public final float factor;
        HeightAlign() {
            factor = ordinal() * 0.5f;
        }
    }
    
    public float width = 0.0f, height = 0.0f;
    
    public float x = 0, y = 0;
    
    public float pivotX = 0, pivotY = 0;
    
    public float scale = 1.0f;
    
    /**
     * Whether the widget should be drawed.
     */
    public boolean doesDraw = true;
    
    /**
     * Whether the widget listens to key events. Note you can't listen to key events either when doesDraw=false.
     */
    public boolean doesListenKey = true;
    
    public WidthAlign alignWidth = WidthAlign.LEFT;
    
    public HeightAlign alignHeight = HeightAlign.TOP;

    public Transform() {
        super("Transform");
    }
    
    //Helper set methods
    public Transform setPos(float _x, float _y) {
        x = _x;
        y = _y;
        return this;
    }
    
    public Transform setSize(float _width, float _height) {
        width = _width;
        height = _height;
        return this;
    }
    
    public void setCenteredAlign() {
        alignWidth = WidthAlign.CENTER;
        alignHeight = HeightAlign.CENTER;
    }
}
