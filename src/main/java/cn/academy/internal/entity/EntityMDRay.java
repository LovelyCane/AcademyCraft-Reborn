package cn.academy.internal.entity;

import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntityMDRay extends EntityRayBase {
    public EntityMDRay(EntityPlayer spawner, double length) {
        super(spawner);
        Vec3d start = VecUtils.add(spawner.getPositionEyes(1F), spawner.getLookVec()), end = VecUtils.add(spawner.getPositionVector(), VecUtils.multiply(spawner.getLookVec(), length));
        this.setFromTo(start, end);
        this.blendInTime = 200;
        this.blendOutTime = 700;
        this.life = 50;
        this.length = length;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (RandUtils.nextDouble() < 0.8) {
            Particle p = MdParticleFactory.INSTANCE.next(world, VecUtils.lookingPos(this, RandUtils.ranged(0, 10)), new Vec3d(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            world.spawnEntity(p);
        }
    }
}