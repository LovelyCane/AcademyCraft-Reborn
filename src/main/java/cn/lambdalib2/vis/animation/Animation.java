package cn.lambdalib2.vis.animation;

/**
 * Base interface for time-based animation. Provide a time point, and the animation modifies the value based on the parameter.
 * @author WeAthFolD
 */
public abstract class Animation {
    public boolean disposed = false;
    
    /**
     * Perform the animation at the given timepoint.
     */
    public abstract void perform(double timePoint);
}