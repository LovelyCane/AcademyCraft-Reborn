package cn.academy.internal.entity;

import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.util.math.Vec3d;

/**
 * States used in RenderRay classes.
 * The view direction of the ray is determined by the rotationYaw and rotationPitch.
 * @author WeAthFolD
 */
public interface IRay extends ViewOptimize.IAssociatePlayer {
    void onRenderTick();
    
    Vec3d getRayPosition();
    
    /**
     * @return If this ray is spawned at player's hand and need to be treated differently for 1st and 3rd person
     */
    boolean needsViewOptimize();
    
    double getLength();
    
    //---TileEntityDeveloperAdvanced parameters
    /**
     * @return An alpha multiplier. Can be used for blend out.
     */
    double getAlpha();
    
    /**
     * @return The alpha multiplier of the glow texture.
     */
    double getGlowAlpha();
    
    /**
     * Get the advance distance of the ray starting point. Can be used for blend out.
     */
    double getStartFix();
    
    /**
     * Get the current ray width multiplier. Used for blending
     */
    double getWidth();
}