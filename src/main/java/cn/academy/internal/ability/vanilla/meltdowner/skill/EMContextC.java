package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityMdRaySmall;
import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.academy.internal.client.renderer.util.ACRenderingHelper;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.ability.vanilla.meltdowner.skill.EMContext.MSG_EFFECT_SPAWN;
import static cn.academy.internal.ability.vanilla.meltdowner.skill.EMContext.MSG_EFFECT_UPDATE;
import static cn.lambdalib2.util.RandUtils.ranged;
import static cn.lambdalib2.util.RandUtils.rangei;

@SideOnly(Side.CLIENT)
@RegClientContext(EMContext.class)
@SuppressWarnings("unused")
public class EMContextC extends ClientContext {
    public EMContextC(EMContext par) {
        super(par);
    }

    @Listener(channel = MSG_EFFECT_UPDATE, side = Side.CLIENT)
    private void c_updateEffect() {
        int count = rangei(1, 3);
        while (count-- > 0) {
            double r = ranged(0.5, 1);
            double theta = ranged(0, Math.PI * 2);
            double h = ranged(-1.2, 0);
            Vec3d pos = VecUtils.add(new Vec3d(player.posX, player.posY + ACRenderingHelper.getHeightFix(player), player.posZ), new Vec3d(r * Math.sin(theta), h, r * Math.cos(theta)));
            Vec3d vel = new Vec3d(ranged(-0.02, 0.02), ranged(0.01, 0.05), ranged(-0.02, 0.02));
            player.world.spawnEntity(MdParticleFactory.INSTANCE.next(player.world, pos, vel));
        }
    }

    @Listener(channel = MSG_EFFECT_SPAWN, side = Side.CLIENT)
    private void c_spawnRay(Vec3d from, Vec3d to) {
        EntityMdRaySmall ray = new EntityMdRaySmall(world());
        ray.setFromTo(from, to);
        world().spawnEntity(ray);
    }
}
