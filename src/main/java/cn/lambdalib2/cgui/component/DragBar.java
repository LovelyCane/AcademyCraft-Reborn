package cn.lambdalib2.cgui.component;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.annotation.CGuiEditorComponent;
import cn.lambdalib2.cgui.event.DragEvent;
import cn.lambdalib2.cgui.event.GuiEvent;
import cn.lambdalib2.util.MathUtils;

@CGuiEditorComponent
public class DragBar extends Component {

    public static class DraggedEvent implements GuiEvent {}

    public enum Axis { X, Y }

    /**
     * Lower and upper bound of the drag area.
     */
    public float lower, upper;
    public Axis axis = Axis.Y;

    /**
     * Progress of the drag bar, ranging from 0 to 1.
     */
    public float progress = 0.0f;

    public DragBar(Axis _axis, float _y0, float _y1) {
        this();
        axis  = _axis;
        lower = _y0;
        upper = _y1;
    }

    public DragBar() {
        super("DragBar");

        listen(DragEvent.class, (w, event) -> {
            float original = (axis == Axis.X) ? w.transform.y : w.transform.x;

            w.getGui().updateDragWidget();

            if (axis == Axis.X) {
                w.transform.y = original;
                w.transform.x = MathUtils.clampf(lower, upper, w.transform.x);
            } else {
                w.transform.x = original;
                w.transform.y = MathUtils.clampf(lower, upper, w.transform.y);
            }

            // Update progress based on widget position
            updateProgress(w);

            w.getGui().updateWidget(w);
            w.post(new DraggedEvent());
        });
    }

    /**
     * Get the current progress of the drag bar.
     * The result is clamped between 0 and 1.
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Set the progress of the drag bar.
     * The progress value should be between 0 and 1.
     */
    public void setProgress(float prg) {
        // Clamp the progress value and update internal state
        progress = MathUtils.clampf(0, 1, prg);
        float val = lower + (upper - lower) * progress;
        if (axis == Axis.X) {
            widget.transform.x = val;
        } else {
            widget.transform.y = val;
        }

        widget.dirty = true;
    }

    /**
     * Set the drag area.
     */
    public DragBar setArea(float _lower, float _upper) {
        lower = _lower;
        upper = _upper;
        return this;
    }

    public static DragBar get(Widget w) {
        return w.getComponent(DragBar.class);
    }

    /**
     * Update the progress variable based on the current widget position.
     */
    private void updateProgress(Widget w) {
        float position = (axis == Axis.X) ? w.transform.x : w.transform.y;
        progress = MathUtils.clampf(0, 1, (position - lower) / (upper - lower));
    }
}
