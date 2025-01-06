package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.internel.render.particle.MdParticleFactory;
import cn.academy.internel.sound.ACSounds;
import cn.academy.internel.sound.FollowEntitySound;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.entityx.handlers.Rigidbody;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public abstract class MRContextC extends ClientContext {
    private FollowEntitySound loopSound;
    private Entity ray;

    public MRContextC(MRContext par) {
        super(par);
    }

    protected abstract Entity createRay();

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void c_start() {
        ray = createRay();
        world().spawnEntity(ray);
        loopSound = new FollowEntitySound(player, "md.mine_loop", SoundCategory.PLAYERS).setLoop().setVolume(0.3f);
        ACSounds.playClient(loopSound);
        ACSounds.playClient(player, "md.mine_" + ((MineRaysBase) skill).getPostfix() + "_startup", SoundCategory.AMBIENT, 0.4f);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_update() {
        // Do nothing for now
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_end() {
        ray.setDead();
        loopSound.stop();
    }

    @Listener(channel = MRContext.MSG_PARTICLES, side = Side.CLIENT)
    private void c_spawnParticles(int x, int y, int z) {
        int max = RandUtils.rangei(2, 3);
        for (int i = 0; i <= max; i++) {
            double _x = x + RandUtils.ranged(-0.2, 1.2);
            double _y = y + RandUtils.ranged(-0.2, 1.2);
            double _z = z + RandUtils.ranged(-0.2, 1.2);
            Particle p = MdParticleFactory.INSTANCE.next(world(), new Vec3d(_x, _y, _z), new Vec3d(RandUtils.ranged(-0.06, 0.06), RandUtils.ranged(-0.06, 0.06), RandUtils.ranged(-0.06, 0.06)));
            if (((MineRaysBase) skill).getParticleTexture() != null) {
                p.texture = (((MineRaysBase) skill).getParticleTexture());
            }
            p.needRigidbody = false;
            Rigidbody rb = new Rigidbody();
            rb.gravity = 0.01;
            rb.entitySel = null;
            rb.blockFil = null;
            p.addMotionHandler(rb);
            world().spawnEntity(p);
        }
    }
}
