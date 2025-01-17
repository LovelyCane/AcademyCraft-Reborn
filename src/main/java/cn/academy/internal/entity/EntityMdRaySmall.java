package cn.academy.internal.entity;

import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntityMdRaySmall extends EntityRayBase {
    public EntityMdRaySmall(World world) {
        super(world);
        this.blendInTime = 200;
        this.blendOutTime = 400;
        this.life = 14;
        this.length = 15.0;
    }
    
    @Override
    protected void onFirstUpdate() {
        super.onFirstUpdate();
        ACSounds.playClient(world,posX, posY, posZ, "md.ray_small",SoundCategory.AMBIENT, 0.8f,1.0f);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        Particle p = MdParticleFactory.INSTANCE.next(world,
//            new Motion3D(this, true).move(RandUtils.ranged(0, 10)).getPosVec(),
                VecUtils.lookingPos(this, RandUtils.ranged(0, 10)),
            new Vec3d(RandUtils.ranged(-.015, .015), RandUtils.ranged(-.015, .015), RandUtils.ranged(-.015, .015)));
        world.spawnEntity(p);
    }
    
    @Override
    public double getWidth() {
        long dt = getDeltaTime();
        int blendTime = 500;

        if(dt > this.life * 50 - blendTime) {
            double timeFactor = MathUtils.clampd(0, 1, (double) (dt - (this.life * 50 - blendTime)) / blendTime);
            return 1 - timeFactor;
        }
        
        return 1.0;
    }
}