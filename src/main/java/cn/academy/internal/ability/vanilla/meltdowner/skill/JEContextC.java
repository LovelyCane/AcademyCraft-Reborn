package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityDiamondShield;
import cn.academy.internal.entity.EntityRippleMark;
import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.ability.vanilla.meltdowner.skill.JEContext.MSG_MARK_END;
import static cn.academy.internal.ability.vanilla.meltdowner.skill.JEContext.MSG_TRIGGER;

@SideOnly(Side.CLIENT)
@RegClientContext(JEContext.class)
@SuppressWarnings("unused")
public class JEContextC extends ClientContext {

    private static final float TIME = 8;

    private EntityRippleMark mark;
    private Vec3d target;
    private final Vec3d start;
    private boolean isMarking = false;
    private int ticks = 0;

    private EntityDiamondShield entity;
    private boolean isTriggering = false;

    public JEContextC(JEContext par) {
        super(par);
        this.start = new Vec3d(player.posX, player.posY, player.posZ);
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void l_spawnMark() {
        if (isLocal()) {
            isMarking = true;
            mark = new EntityRippleMark(world());
            world().spawnEntity(mark);
            mark.color.set(51, 255, 51, 179);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void l_updateMark() {
        if (isLocal() && isMarking) {
            Vec3d dest = getDest();
            mark.setPosition(dest.x, dest.y, dest.z);
        }
    }

    @Listener(channel = MSG_MARK_END, side = Side.CLIENT)
    private void l_endMark() {
        if (isLocal()) {
            isMarking = false;
            mark.setDead();
        }
    }

    private Vec3d getDest() {
        return Raytrace.getLookingPos(player, 12, EntitySelectors.nothing()).getLeft();
    }

    @Listener(channel = MSG_TRIGGER, side = Side.CLIENT)
    private void c_tStartEffect(Vec3d _target) {
        target = _target;
        isTriggering = true;
        entity = new EntityDiamondShield(player);
        world().spawnEntity(entity);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_tUpdateEffect() {
        if (isTriggering) {
            ticks++;
            if (isLocal()) {
                player.capabilities.setPlayerWalkSpeed(0.07f);
            }
            for (int i = 0; i <= 10; i++) {
                Vec3d pos2 = VecUtils.lerp(start, target, 3 * ticks / TIME);
                Particle p = MdParticleFactory.INSTANCE.next(world(), VecUtils.add(new Vec3d(player.posX, player.posY, player.posZ), new Vec3d(RandUtils.ranged(-0.3, 0.3), RandUtils.ranged(-0.3, 0.3), RandUtils.ranged(-0.3, 0.3))), new Vec3d(RandUtils.ranged(-0.02, 0.02), RandUtils.ranged(-0.02, 0.02), RandUtils.ranged(-0.02, 0.02)));
                world().spawnEntity(p);
            }
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_tEndEffect() {
        if (mark != null)
            mark.setDead();

        if (isTriggering) {
            if (isLocal()) {
                player.capabilities.setPlayerWalkSpeed(0.1f);
            }
            entity.setDead();
        }
        isTriggering = false;
    }
}