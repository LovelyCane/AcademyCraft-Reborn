package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.entity.EntityMdBall;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SBContext extends Context<ScatterBomb> {
    private static final float MIN_DAMAGE = 5.0f;
    private static final float MAX_DAMAGE = 9.0f;
    private static final int MAX_TICKS = 80;
    private static final int MOD = 10;
    private static final double RAY_RANGE = 15.0;

    private final List<EntityMdBall> balls = new ArrayList<>();
    private final Predicate<Entity> basicSelector = EntitySelectors.everything();
    private int ticks = 0;
    private final float exp = ctx.getSkillExp();
    private float overloadKeep = 0f;

    public SBContext(EntityPlayer p) {
        super(p, ScatterBomb.INSTANCE);
    }

    private float getDamage(float exp) {
        return MathUtils.lerpf(MIN_DAMAGE, MAX_DAMAGE, exp);
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onKeyUp() {
        terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_onStart() {
        float overload = MathUtils.lerpf(80, 60, exp);
        ctx.consume(overload, 0);
        overloadKeep = ctx.cpData.getOverload();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if (ctx.cpData.getOverload() < overloadKeep) {
            ctx.cpData.setOverload(overloadKeep);
        }
        ticks++;

        if (ticks <= MAX_TICKS) {
            if (ticks >= 20 && ticks % MOD == 0) {
                EntityMdBall ball = new EntityMdBall(player);
                world().spawnEntity(ball);
                balls.add(ball);
            }
            float cp = MathUtils.lerpf(3, 6, exp);
            if (!ctx.consume(0, cp)) terminate();
        }

        if (ticks == 200) {
            player.attackEntityFrom(DamageSource.causePlayerDamage(player), 6);
            terminate();
        }
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.SERVER)
    private void s_onEnd() {
        int autoCount = exp > 0.5 ? (int) (balls.size() * exp) : 0;
        List<Entity> autoTarget = exp > 0.5 ? WorldUtils.getEntities(player, 5, EntitySelectors.exclude(player).and(t -> t instanceof EntityLiving)) : new ArrayList<>();

        for (EntityMdBall ball : balls) {
            Vec3d dest = newDest();
            if (autoCount > 0 && !autoTarget.isEmpty()) {
                Entity target = autoTarget.get(RandUtils.nextInt(autoTarget.size()));
                dest = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
                autoCount--;
            }

            RayTraceResult traceResult = Raytrace.perform(world(), new Vec3d(ball.posX, ball.posY + ball.getEyeHeight(), ball.posZ),
                    dest, basicSelector.and(EntitySelectors.exclude(player)));

            if (traceResult.entityHit != null) {
                traceResult.entityHit.hurtResistantTime = -1;
                MDDamageHelper.attack(ctx, traceResult.entityHit, getDamage(exp));
            }

            NetworkMessage.sendToAllAround(
                    TargetPoints.convert(player, 25),
                    SBNetDelegate.INSTANCE,
                    SBNetDelegate.MSG_EFFECT,
                    ball.getPositionEyes(1F),
                    dest
            );
            ball.setDead();
        }

        ctx.addSkillExp(0.001f * balls.size());
    }

    private Vec3d newDest() {
        Vec3d begin = VecUtils.lookingPos(player, RAY_RANGE);
        Vec3d look = player.getLookVec()
                .rotatePitch(MathUtils.toRadians((RandUtils.nextFloat() - 0.5F) * 25))
                .rotateYaw(MathUtils.toRadians((RandUtils.nextFloat() - 0.5F) * 25));

        return begin.add(look.scale(RAY_RANGE));
    }
}
