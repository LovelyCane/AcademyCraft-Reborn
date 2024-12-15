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

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.event.FrameEvent;
import org.lwjgl.util.Color;

/**
 * @author WeAthFolD
 */
@CGuiEditorComponent
public class Tint extends Component {
    
    public Color
        idleColor,
        hoverColor;
    
    public boolean affectTexture = false;

    public double zLevel = 0.0;

    public Tint() {
        this(Colors.fromFloat(1, 1, 1, 0.6f), Colors.fromFloat(1, 1, 1, 1));
    }

    public Tint(Color idle, Color hover, boolean _affectTexture) {
        this(idle, hover);
        affectTexture = _affectTexture;
    }
    
    public Tint(Color idle, Color hover) {
        super("Tint");

        idleColor = idle;
        hoverColor = hover;
        
        listen(FrameEvent.class, (w, event) -> {
            if(affectTexture) {
                DrawTexture dt = w.getComponent(DrawTexture.class);
                if(dt != null) {
                    dt.color = event.hovering ? hoverColor : idleColor;
                }
            } else {
                if(event.hovering) Colors.bindToGL(hoverColor);
                else Colors.bindToGL(idleColor);
                
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                HudUtils.pushZLevel();
                HudUtils.zLevel = zLevel;
                HudUtils.colorRect(0, 0, w.transform.width, w.transform.height);
                HudUtils.popZLevel();
            }
        });
    }

    public Tint setAffectTexture() {
        affectTexture = true;
        return this;
    }
}
