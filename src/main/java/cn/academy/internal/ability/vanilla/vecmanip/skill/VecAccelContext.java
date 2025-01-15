package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.ability.context.IConsumptionProvider;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;


@SuppressWarnings("unused")
public class VecAccelContext extends Context<VecAccel> implements IConsumptionProvider {
    public static final String MSG_PERFORM = "perform";
    public static final double MAX_VELOCITY = 2.5;
    public static final int MAX_CHARGE = 20;
    public static final double PLAYER_ACCEL = -0.08;
    public static final double DAMPING = 0.9;
    public static final double LN_A = Math.log(DAMPING);

    private int ticker = 0;
    public boolean canPerform = true;

    public VecAccelContext(EntityPlayer player) {
        super(player, VecAccel.INSTANCE);
    }

    @Override
    public float getConsumptionHint() {
        return (float) consumption;
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    public void l_keyUp() {
        l_perform();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    public void l_keyAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    public void l_tickHandler() {
        if (isLocal()) {
            ticker++;
            updateCanPerform();
        }
    }

    public void l_perform() {
        if (canPerform && consume()) {
            VecUtils.setMotion(player, initSpeed(0.0f));
            player.getLowestRidingEntity();
            ctx.setCooldown((int) MathUtils.lerpf(80, 50, ctx.getSkillExp()));
            sendToServer(MSG_PERFORM);
        } else {
            terminate();
        }
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    public void s_perform() {
        consume();
        player.fallDistance = 0;
        ctx.addSkillExp(0.002f);
        sendToClient(MSG_PERFORM);
        terminate();
    }

    public Vec3d initSpeed(float partialTicks) {
        float lookYaw = MathUtils.lerpf(player.prevRotationYaw, player.rotationYaw, partialTicks);
        float lookPitch = MathUtils.lerpf(player.prevRotationPitch, player.rotationPitch, partialTicks) - 10;
        Vec3d look = new EntityLook(lookYaw, lookPitch).toVec3();
        return VecUtils.multiply(look, speed());
    }

    private double speed() {
        double prog = MathUtils.lerp(0.4, 1, MathUtils.clampd(0, 1, ticker / (double) MAX_CHARGE));
        return Math.sin(prog) * MAX_VELOCITY;
    }

    private boolean consume() {
        double overload = MathUtils.lerpf(30, 15, ctx.getSkillExp());
        return ctx.consume((float) overload, (float) consumption);
    }

    private final double consumption = MathUtils.lerpf(120, 80, ctx.getSkillExp());

    private void updateCanPerform() {
        canPerform = ignoreGroundChecking || checkGround();
    }

    private final boolean ignoreGroundChecking = ctx.getSkillExp() > 0.5f;

    private boolean checkGround() {
        Vec3d p0 = player.getPositionVector();
        Vec3d p1 = new Vec3d(p0.x, p0.y - 2, p0.z);
        RayTraceResult traceResult = Raytrace.perform(world(), p0, p1, EntitySelectors.nothing(), BlockSelectors.filNothing);
        return traceResult.typeOfHit == RayTraceResult.Type.BLOCK;
    }
}
