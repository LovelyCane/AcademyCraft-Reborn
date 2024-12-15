/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.component;

import cn.lambdalib2.cgui.annotation.CGuiEditorComponent;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.HudUtils;
import org.lwjgl.opengl.GL11;

import cn.lambdalib2.cgui.event.FrameEvent;
import org.lwjgl.util.Color;

/**
 * @author WeAthFolD
 */
@CGuiEditorComponent
public class Outline extends Component {
    
    public Color color;
    public float lineWidth = 2;

    public Outline() {
        this(Colors.white());
    }

    public Outline(Color _color) {
        super("Outline");

        color = _color;
        
        listen(FrameEvent.class, (w, e) -> {
            Colors.bindToGL(color);
            HudUtils.drawRectOutline(0, 0, w.transform.width, w.transform.height, lineWidth);
            GL11.glColor4f(1, 1, 1, 1);
        });
    }

}
