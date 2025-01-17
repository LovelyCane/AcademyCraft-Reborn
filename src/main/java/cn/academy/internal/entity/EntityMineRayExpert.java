package cn.academy.internal.entity;

import cn.academy.internal.client.renderer.entity.RenderEntityMineRayExpert;
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
public class EntityMineRayExpert extends EntityRayBase {
    public static RenderEntityMineRayExpert renderer;

    public EntityMineRayExpert(EntityPlayer _player) {
        super(_player);

        this.blendInTime = 200;
        this.blendOutTime = 400;
        this.life = 233333;
        this.length = 15.0;

        updatePos();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updatePos();

        EntityPlayer player = getPlayer();
        if (RandUtils.nextDouble() < 0.6) {
            Particle p = MdParticleFactory.INSTANCE.next(world, VecUtils.lookingPos(player, RandUtils.ranged(0, 10)), new Vec3d(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            world.spawnEntity(p);
        }
    }

    private void updatePos() {
        EntityPlayer player = getPlayer();
        Vec3d end = VecUtils.lookingPos(player, 15);
        this.setFromTo(player.posX, player.posY + 1.55, player.posZ, end.x, end.y, end.z);
    }
}