/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.component;

import cn.lambdalib2.cgui.annotation.CGuiEditorComponent;
import cn.lambdalib2.cgui.event.DragEvent;

/**
 * This component simply updates the widget's position when it was dragged.
 * @author WeAthFolD
 */
@CGuiEditorComponent
public class Draggable extends Component {

    public Draggable() {
        super("Draggable");
        
        listen(DragEvent.class, (w, e) -> {
            w.getGui().updateDragWidget();
            w.dirty = true;
        });
    }

}
