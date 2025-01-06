package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.academy.entity.EntityMdBall;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static cn.lambdalib2.util.EntitySelectors.living;

@SuppressWarnings("unused")
public class EMContext extends Context<ElectronMissile> {
    private static final int MAX_HOLD = 5;
    public static final String MSG_EFFECT_SPAWN = "effect_spawn";
    public static final String MSG_EFFECT_UPDATE = "effect_update";
    private final LinkedList<EntityMdBall> active;
    private int ticks;

    private final float exp;
    private final float consumption;
    private final float overload_attacked;
    private final float consumption_attacked;

    private float overloadKeep = 0f;

    public EMContext(EntityPlayer p) {
        super(p, ElectronMissile.INSTANCE);
        this.exp = ctx.getSkillExp();
        this.consumption = MathUtils.lerpf(12, 5, exp);
        this.overload_attacked = MathUtils.lerpf(9, 4, exp);
        this.consumption_attacked = MathUtils.lerpf(60, 25, exp);
        this.active = new LinkedList<>();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onEnd() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_madeAlive() {
        float overload_keep = 200;
        ctx.consume(overload_keep, 0);
        overloadKeep = ctx.cpData.getOverload();
        active.clear();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if (ctx.cpData.getOverload() < overloadKeep) {
            ctx.cpData.setOverload(overloadKeep);
        }
        if (!ctx.consume(0, consumption)) {
            terminate();
        } else {
            int timeLimit = (int) MathUtils.lerpf(80, 200, exp);
            if (ticks <= timeLimit) {
                if (ticks % 10 == 0 && active.size() < MAX_HOLD) {
                    EntityMdBall ball = new EntityMdBall(player);
                    player.world.spawnEntity(ball);
                    active.add(ball);
                }
                if (ticks != 0 && ticks % 8 == 0) {
                    float range = MathUtils.lerpf(5, 13, exp);
                    List<Entity> list = WorldUtils.getEntities(player, range, EntitySelectors.exclude(player).and(living()));
                    if (!active.isEmpty() && !list.isEmpty() && ctx.consume(overload_attacked, consumption_attacked)) {
                        double min = Double.MAX_VALUE;
                        Entity result = null;
                        for (Entity e : list) {
                            double dist = e.getDistanceSq(player);
                            if (dist < min) {
                                min = dist;
                                result = e;
                            }
                        }
                        // Find a random ball and destroy it
                        int index = 1 + RandUtils.nextInt(active.size());
                        Iterator<EntityMdBall> iter = active.iterator();
                        EntityMdBall ball = null;
                        for (int i = 0; i < index; i++) {
                            ball = iter.next();
                        }
                        iter.remove();

                        // client action
                        sendToClient(EMContext.MSG_EFFECT_SPAWN, VecUtils.entityPos(Objects.requireNonNull(ball)), VecUtils.add(VecUtils.entityPos(Objects.requireNonNull(result)), new Vec3d(0, result.getEyeHeight(), 0)));
                        // server action
                        result.hurtResistantTime = -1;
                        float damage = MathUtils.lerpf(10, 18, exp);
                        MDDamageHelper.attack(ctx, result, damage);
                        ctx.addSkillExp(0.001f);
                        ball.setDead();
                    }
                }
            } else {
                terminate();
            }
            sendToClient(EMContext.MSG_EFFECT_UPDATE);
            ticks++;
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.SERVER)
    private void s_onEnd() {
        int cooldown = MathUtils.clampi(700, 400, (int) exp);
        ctx.setCooldown(cooldown);

        for (EntityMdBall ball : active) {
            ball.setDead();
        }
    }
}
