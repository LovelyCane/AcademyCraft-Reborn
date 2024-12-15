/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui.component;

import java.util.*;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.annotation.CGuiEditorComponent;
import cn.lambdalib2.cgui.event.GuiEvent;
import cn.lambdalib2.util.MathUtils;
import com.google.common.collect.ImmutableList;

/**
 * Component that can hold widgets itself and display them as a list. Only Widgets fully in the area will be shown.
 * You can add Widgets both before adding the component and in runtime.
 * @author WeAthFolD
 */
@CGuiEditorComponent
public class ElementList extends Component {
    
    private List<Widget> subWidgets = new LinkedList<>();
    
    /**
     * The fixed vertical spacing between widgets.
     */
    public float spacing = 0.0f;
    
    private int progress;
    
    private boolean loaded = false;

    public ElementList() {
        super("ElementList");
    }

    @Override
    public void onAdded() {
        super.onAdded();

        loaded = true;
        for(Widget ww : subWidgets) {
            widget.addWidget(ww);
        }
        updateList();
    }

    public int getProgress() {
        return progress;
    }
    
    public int getMaxProgress() {
        float sum = 0.0f;

        int i = subWidgets.size() - 1;
        while (i >= 0) {
            sum += subWidgets.get(i).transform.height + spacing;

            if (sum >= widget.transform.height) {
                return i == subWidgets.size() - 1 ? i : i + 1;
            }

            --i;
        }

        return 0;
    }
    
    public void progressNext() {
        setProgress(progress + 1);
    }
    
    public void progressLast() {
        setProgress(progress - 1);
    }
    
    public void setProgress(int newProgress) {
        newProgress = MathUtils.clampi(0, getMaxProgress(), newProgress);
        boolean shouldUpdate = loaded && progress != newProgress;
        progress = newProgress;
        if(shouldUpdate) {
            updateList();
        }
    }

    /**
     * @return A immutable list of widgets managed by this ElementList.
     */
    public List<Widget> getSubWidgets() {
        return ImmutableList.copyOf(subWidgets);
    }

    public int size() {
        return subWidgets.size();
    }
    
    private void updateList() {
        float sum = 0.0f;
        for(Widget w : subWidgets) {
            w.transform.doesDraw = false;
        }
        
        for(int i = progress;
            i < subWidgets.size() &&
                    (sum + subWidgets.get(i).transform.height) <= this.widget.transform.height;
            ++i) {
            Widget w = subWidgets.get(i);
            
            w.transform.doesDraw = true;
            w.transform.x = 0;
            w.transform.y = sum;
            w.dirty = true;
            
            sum += w.transform.height + spacing;
        }
    }
    
    @Override
    public void onRemoved() {
        for(Widget w : subWidgets) {
            w.dispose();
        }
    }
    
    public static class ProgressChangedEvent implements GuiEvent  {}
    
    public void addWidget(Widget w) {
        preAdd(w);
        subWidgets.add(w);
        postAdd(w);
    }

    /**
     * Add this widget before the given widget. If the given widget is not in the list, append to the end.
     */
    public void addWidgetAfter(Widget pivot, Widget... ws) {
        for(Widget w : ws) preAdd(w);

        ListIterator<Widget> itr = subWidgets.listIterator();
        boolean added = false;
        while(itr.hasNext()) {
            Widget iw = itr.next();
            if(iw == pivot) {
                for(Widget w : ws) itr.add(w);
                added = true;
                break;
            }
        }
        if(!added) {
            Collections.addAll(subWidgets, ws);
        }

        for(Widget w : ws) postAdd(w);
    }

    private void preAdd(Widget w) {
        w.needCopy = false; // As it's handled by the component, copying leads to chaos
    }

    private void postAdd(Widget w) {
        if (loaded) {
            widget.addWidget(w);
            updateList();
        }
    }
    
    public float getFullHeight() {
        return sumHeight(0, subWidgets.size());
    }

    public boolean shouldScroll() {
        return widget.transform.height < getFullHeight();
    }
    
    /**
     * from inclusive, to exclusive
     */
    private float sumHeight(int from, int to) {
        float ret = 0.0f;
        for(int i = from; i < to; ++i) {
            ret += subWidgets.get(i).transform.height;
            ret += spacing;
        }
        if(to == subWidgets.size())
            ret -= spacing;
        return ret;
    }
    
    public ElementList copy() {
        ElementList el = (ElementList) super.copy();
        for(Widget w : subWidgets) {
            el.addWidget(w);
        }
        return el;
    }

}
