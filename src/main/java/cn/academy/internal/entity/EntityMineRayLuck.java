package cn.academy.internal.entity;

import cn.academy.Resources;
import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntityMineRayLuck extends EntityRayBase {
    static final ResourceLocation texture = Resources.getTexture("effects/md_particle_luck");

    public EntityMineRayLuck(EntityPlayer _player) {
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
        EntityPlayer player = getPlayer();
        updatePos();

        if (RandUtils.nextDouble() < 0.6) {
            Particle p = MdParticleFactory.INSTANCE.next(world, VecUtils.lookingPos(player, RandUtils.ranged(0, 10)), new Vec3d(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            p.texture = texture;
            world.spawnEntity(p);
        }
    }

    private void updatePos() {
        EntityPlayer player = getPlayer();
        Vec3d end = VecUtils.lookingPos(player, 15);
        this.setFromTo(player.posX, player.posY + 1.55, player.posZ, end.x, end.y, end.z);
    }
}