package cn.academy.internal.entity;

import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.util.MathUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 *
 */
@SideOnly(Side.CLIENT)
public class EntityBarrageRayPre extends EntityRayBase {
    public EntityBarrageRayPre(World world, boolean hit) {
        super(world);

        this.blendInTime = 200;
        this.blendOutTime = 400;
        this.life = hit ? 50 : 30;
        this.length = 15.0;
    }

    @Override
    protected void onFirstUpdate() {
        super.onFirstUpdate();
        ACSounds.playClient(world,posX, posY, posZ, "md.ray_small",SoundCategory.AMBIENT, 0.8f,1.0f);
    }

    @Override
    public double getWidth() {
        double dt = getDeltaTime();
        int blendTime = 500;

        if(dt > this.life * 50 - blendTime) {
            return 1 - MathUtils.clampd(1, 0, (dt - (this.life * 50 - blendTime)) / blendTime);
        }

        return 1.0;
    }
}