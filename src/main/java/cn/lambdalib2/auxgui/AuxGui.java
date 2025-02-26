package cn.lambdalib2.auxgui;

import cn.lambdalib2.util.GameTimer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Auxiliary GUI interface class. This is a kind of GUI that doesn't make mouse gain focus. </br>
 * GUIs such as health indication, information indications are suitable of using this interface to define.
 * The class also provided a set of key-listening functions, based on LIKeyProcess. you can use event-based
 * methods to set up key listening.
 * @author WeathFolD
 */

@SideOnly(Side.CLIENT)
public abstract class AuxGui {
    // Intrusive states
    boolean lastFrameActive = false;
    double lastActivateTime;
    
    // Parameters
    /**
     * Whether this AuxGui needs fixed time step update (ticking). If set to true onTick() method will get called each onTick.
     */
    public boolean requireTicking = false;

    /**
     * Consistent GUIs won't get removed when player is dead.
     */
    public boolean consistent = true;

    /**
     * Judge if this GUI is a foreground GUI and interrupts key listening.
     */
    public boolean foreground = false;

    public boolean disposed;

    public AuxGui() {}

    public void dispose() {
        disposed = true;
    }

    public double getTimeActive() {
        return GameTimer.getTime() - lastActivateTime;
    }

    /**
     * Called when this AuxGui instance is literally removed from the draw list.
     */
    public void onDisposed() {
    }
    
    /**
     * Called when this AuxGui instance is literally added into the draw list.
     */
    public void onEnable() {
    }

    public void onTick() {
    }

    public abstract void draw(ScaledResolution resolution);
}
