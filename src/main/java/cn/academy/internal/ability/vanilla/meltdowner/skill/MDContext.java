package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.util.RangedRayDamage;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;

import static cn.lambdalib2.util.MathUtils.lerpf;

@SuppressWarnings("unused")
public class MDContext extends Context<Meltdowner> {
    public static final String MSG_PERFORM = "perform";
    public static final String MSG_REFLECTED = "reflect";
    public static final int TICKS_MIN = 20;
    public static final int TICKS_MAX = 40;
    public static final int TICKS_TOLE = 100;

    private int ticks = 0;
    private final float exp = ctx.getSkillExp();
    private final float tickConsumption = lerpf(10, 15, exp);

    private float overloadKeep = 0f;

    public MDContext(EntityPlayer player) {
        super(player, Meltdowner.INSTANCE);
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_madeAlive() {
        float overload = lerpf(200, 170, exp);
        ctx.consume(overload, 0);
        overloadKeep = ctx.cpData.getOverload();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_keyUp() {
        if (ticks >= TICKS_MIN)
            sendToServer(MSG_PERFORM);
        else
            terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_keyAbort() {
        terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TICK, side = {Side.CLIENT, Side.SERVER})
    private void g_tick() {
        ticks++;
        if (!isRemote()) {
            if (ctx.cpData.getOverload() < overloadKeep)
                ctx.cpData.setOverload(overloadKeep);
            if (!ctx.consume(0, tickConsumption) || ticks > TICKS_TOLE)
                terminate();
        }
    }

    @NetworkMessage.Listener(channel = MSG_PERFORM, side = Side.SERVER)
    private void s_perform() {
        int ct = toChargeTicks();
        double[] length = new double[]{30.0};
        RangedRayDamage rrd = new RangedRayDamage.Reflectible(
                ctx,
                lerpf(2, 3, exp),
                getEnergy(ct),
                reflector -> {
                    length[0] = Math.min(length[0], reflector.getDistanceSq(ctx.player));
                    s_reflected(reflector);
                    sendToClient(MSG_REFLECTED, reflector);
                });
        rrd.startDamage = getDamage(ct);
        rrd.perform();
        ctx.addSkillExp(getExpIncr(ct));
        ctx.setCooldown(getCooldown(ct));
        sendToClient(MSG_PERFORM, ct, length[0]);
        terminate();
    }

    private void s_reflected(Entity reflector) {
        RayTraceResult result = Raytrace.traceLiving(reflector, 10);
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            ctx.attack(result.entityHit, 0.5f * lerpf(20, 50, exp));
        }
    }

    private float timeRate(int ct) {
        return lerpf(0.8f, 1.2f, (ct - 20.0f) / 20.0f);
    }

    private float getEnergy(int ct) {
        return timeRate(ct) * lerpf(300, 700, exp);
    }

    private float getDamage(int ct) {
        return timeRate(ct) * lerpf(18, 50, exp);
    }

    private int getCooldown(int ct) {
        return (int) (timeRate(ct) * 20 * lerpf(15, 7, exp));
    }

    private float getExpIncr(int ct) {
        return timeRate(ct) * 0.002f;
    }

    private int toChargeTicks() {
        return Math.min(ticks, TICKS_MAX);
    }
}
