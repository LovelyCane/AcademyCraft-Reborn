package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.datapart.AbilityData;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import static cn.academy.internal.ability.vanilla.electromaster.skill.MagMovement.*;

@SuppressWarnings("unused")
public class MovementContext extends Context<MagMovement> {
    public static final String MSG_EFFECT_START = "effect_start";
    public static final String MSG_EFFECT_UPDATE = "effect_update";

    private boolean canSpawnEffect = false;

    private double mox, moy, moz;
    private final double sx = player.posX;
    private final double sy = player.posY;
    private final double sz = player.posZ;
    private Target target;

    private final float exp = ctx.getSkillExp();
    private final double cp = MathUtils.lerpf(15, 8, exp);
    private final double overload = MathUtils.lerpf(60, 30, exp);

    private float overloadKeep = 0f;

    private static final double velocity = 1d;

    public MovementContext(EntityPlayer p) {
        super(p, MagMovement.INSTANCE);
    }

    private double getExpIncr(double distance) {
        return Math.max(0.005f, 0.0011f * (float) distance);
    }

    private double tryAdjust(double from, double to) {
        double d = to - from;
        if (Math.abs(d) < ACCEL)
            return to;
        return d > 0 ? from + ACCEL : from - ACCEL;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = {Side.SERVER, Side.CLIENT})
    private void g_onStart() {
        ctx.consume((float) overload, 0);
        overloadKeep = ctx.cpData.getOverload();
        AbilityData aData = AbilityData.get(player);
        RayTraceResult result = Raytrace.traceLiving(player, getMaxDistance(aData));
        if (result.typeOfHit != RayTraceResult.Type.MISS) {
            target = toTarget(aData, player.world, result);
            if (target == null) {
                terminate();
            } else {
                canSpawnEffect = true;
            }
        } else {
            terminate();
        }
    }

    @Listener(channel = MSG_EFFECT_START, side = Side.SERVER)
    private void s_onEffectStart() {
        sendToClient(MSG_EFFECT_START);
    }

    @Listener(channel = MSG_EFFECT_UPDATE, side = Side.SERVER)
    private void s_onEffectUpdate(Vec3d targetPos) {
        sendToClient(MSG_EFFECT_UPDATE, targetPos);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_onTick() {
        if (ctx.cpData.getOverload() < overloadKeep)
            ctx.cpData.setOverload(overloadKeep);
        if (canSpawnEffect) {
            sendToServer(MSG_EFFECT_START);
            canSpawnEffect = false;
        }
        if (target != null) {
            target.tick();
            sendToServer(MSG_EFFECT_UPDATE, new Vec3d(target.x, target.y, target.z));
            double dx = target.x - player.posX;
            double dy = target.y - player.posY;
            double dz = target.z - player.posZ;

            double lastMo = MathUtils.lengthSq(player.motionX, player.motionY, player.motionZ);
            if (Math.abs(MathUtils.lengthSq(mox, moy, moz) - lastMo) > 0.5) {
                mox = player.motionX;
                moy = player.motionY;
                moz = player.motionZ;
            }

            double mod = Math.sqrt(dx * dx + dy * dy + dz * dz) / velocity;

            dx /= mod;
            dy /= mod;
            dz /= mod;

            player.motionX = tryAdjust(mox, dx);
            mox = tryAdjust(mox, dx);
            player.motionY = tryAdjust(moy, dy);
            moy = tryAdjust(moy, dy);
            player.motionZ = tryAdjust(moz, dz);
            moz = tryAdjust(moz, dz);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if ((target != null && !target.alive()) || !ctx.consume((float) 0, (float) cp))
            terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.SERVER)
    private void s_onEnd() {
        double traveledDistance = MathUtils.distance(sx, sy, sz, player.posX, player.posY, player.posZ);
        ctx.addSkillExp((float) getExpIncr(traveledDistance));

        player.fallDistance = 0.0f;
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onEnd() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        terminate();
    }
}
