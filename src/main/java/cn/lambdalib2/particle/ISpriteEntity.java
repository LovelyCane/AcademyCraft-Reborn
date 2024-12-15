package cn.lambdalib2.particle;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
public interface ISpriteEntity {

    /**
     * Called each rendering frame before rendering to update the sprite's state
     */
    @SideOnly(Side.CLIENT)
    void updateSprite(Sprite s);

    boolean needViewOptimize();

}
