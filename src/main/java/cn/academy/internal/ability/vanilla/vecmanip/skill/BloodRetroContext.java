package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class BloodRetroContext extends Context<BloodRetrograde> {
    private static final String MSG_PERFORM = "perform";

    private int tick = 0;
    private final float damage = MathUtils.lerpf(30, 60, ctx.getSkillExp());
    EntityPlayer p;
    public BloodRetroContext(EntityPlayer p) {
        super(p, BloodRetrograde.INSTANCE);
        this.p = p;
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    public void l_keyUp() {
        RayTraceResult trace = Raytrace.traceLiving(p, 2);
        if (trace.typeOfHit == RayTraceResult.Type.ENTITY) {
            sendToServer(MSG_PERFORM, trace.entityHit);
        } else {
            terminate();
        }
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    public void s_perform(EntityLivingBase targ) {
        if (consume()) {
            ctx.setCooldown((int) MathUtils.lerpf(90, 40, ctx.getSkillExp()));
            sendToClient(MSG_PERFORM, targ);
            ctx.attack(targ, damage);
            ctx.addSkillExp(0.002f);
        }
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    public void l_tick() {
        if (isLocal()) {
            tick++;

            player.capabilities.setPlayerWalkSpeed(
                    MathUtils.lerpf(0.1f, 0.007f, MathUtils.clampf(0, 1, tick / 20.0f))
            );

            if (tick >= 30) {
                l_keyUp();
            }
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    public void l_terminate() {
        if (isLocal()) {
            player.capabilities.setPlayerWalkSpeed(0.1f);
        }
    }

    private boolean consume() {
        float overload = MathUtils.lerpf(55, 40, ctx.getSkillExp());
        float consumption = MathUtils.lerpf(280, 350, ctx.getSkillExp());
        return ctx.consume(overload, consumption);
    }
}
