package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.ability.context.IConsumptionProvider;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import static cn.lambdalib2.util.VecUtils.setMotion;

@SuppressWarnings("unused")
public class BlastwaveContext extends Context<DirectedBlastwave> implements IConsumptionProvider {
    private static final String MSG_EFFECT = "effect";
    private static final String MSG_PERFORM = "perform";
    private static final String MSG_ATTACK_ENTITY = "entity";
    private static final String MSG_GENERATE_EFFECT_BLOCKS = "effect_blocks";

    private static final int MIN_TICKS = 6;
    private static final int MAX_ACCEPTED_TICKS = 50;
    private static final int MAX_TOLERANT_TICKS = 200;
    private static final int PUNCH_ANIM_TICKS = 6;

    private int ticker = 0;
    private boolean punched = false;
    private int punchTicker = 0;

    public BlastwaveContext(EntityPlayer player) {
        super(player, DirectedBlastwave.INSTANCE);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    public void l_keyUp() {
        if (ticker > MIN_TICKS && ticker < MAX_ACCEPTED_TICKS) {
            sendToServer(MSG_PERFORM, ticker);
        } else {
            terminate();
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    public void l_keyAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    public void l_tick() {
        if (isLocal()) {
            ticker++;
            if (ticker >= MAX_TOLERANT_TICKS) {
                terminate();
            }
            if (punched) {
                punchTicker++;
            }
            if (punched && punchTicker > PUNCH_ANIM_TICKS) {
                terminate();
            }
        }
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    public void s_perform(int ticks) {
        if (tryConsume()) {
            RayTraceResult trace = Raytrace.traceLiving(player, 4, EntitySelectors.living());
            Vec3d position;
            if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
                position = new Vec3d(trace.getBlockPos().getX(), trace.getBlockPos().getY(), trace.getBlockPos().getZ());
            } else if (trace.typeOfHit == RayTraceResult.Type.ENTITY) {
                position = VecUtils.entityHeadPos(trace.entityHit);
            } else {
                position = trace.hitVec;
            }

            ctx.setCooldown(cooldown);
            sendToClient(MSG_PERFORM, position);

            boolean effective = false;

            // Hurt entities around
            List<Entity> entities = WorldUtils.getEntities(world(),
                    position.x, position.y, position.z,
                    3, EntitySelectors.exclude(player));

            for (Entity entity : entities) {
                ctx.attack(entity, damage);
                knockback(entity);

                Vec3d delta = VecUtils.multiply(VecUtils.subtract(entity.getPositionVector(), player.getPositionVector()).normalize(), 0.24);
                setMotion(entity, delta);
                effective = true;
            }
            sendToClient(MSG_ATTACK_ENTITY, entities);

            // Destroy blocks around
            destroyBlocksAround(position);

            sendToClient(MSG_GENERATE_EFFECT_BLOCKS, position);

            ctx.addSkillExp(effective ? 0.0025f : 0.0012f);

        } else {
            terminate();
        }
    }

    private void destroyBlocksAround(Vec3d position) {
        int x = (int) Math.round(position.x);
        int y = (int) Math.round(position.y);
        int z = (int) Math.round(position.z);

        for (int i : ran(x)) {
            for (int j : ran(y)) {
                for (int k : ran(z)) {
                    int dx = i - x, dy = j - y, dz = k - z;
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= 6 && (distSq == 0 || RandUtils.RNG.nextFloat() < breakProb)) {
                        BlockPos bPos = new BlockPos(i, j, k);
                        // logic to break block
                        // logic to drop item if needed
                    }
                }
            }
        }
    }

    @Listener(channel = MSG_ATTACK_ENTITY, side = Side.CLIENT)
    public void c_effect(List<Entity> entities) {
        for (Entity entity : entities) {
            knockback(entity);
        }
    }

    @Listener(channel = MSG_PERFORM, side = Side.CLIENT)
    public void c_perform(Vec3d pos) {
        sendToSelf(MSG_EFFECT, pos);
        punched = true;
    }

    private boolean tryConsume() {
        float overload = MathUtils.lerpf(50, 30, ctx.getSkillExp());
        return ctx.consume(overload, consumption);
    }

    @Override
    public float getConsumptionHint() {
        return consumption;
    }

    private final float consumption = MathUtils.lerpf(160, 200, ctx.getSkillExp());
    private final float breakProb = MathUtils.lerpf(0.5f, 0.8f, ctx.getSkillExp());
    private final float breakHardness = ctx.getSkillExp() < 0.25f ? 2.9f : (ctx.getSkillExp() < 0.5f ? 25f : 55f);
    private final float damage = MathUtils.lerpf(10, 25, ctx.getSkillExp());
    private final float dropRate = MathUtils.lerpf(0.4f, 0.9f, ctx.getSkillExp());
    private final int cooldown = (int) MathUtils.lerpf(80, 50, ctx.getSkillExp());

    private void knockback(Entity targ) {
        Vec3d delta = VecUtils.subtract(VecUtils.entityHeadPos(player), VecUtils.entityHeadPos(targ)).normalize();
        delta = new Vec3d(delta.x, delta.y - 0.4f, delta.z).normalize();
        targ.setPosition(targ.posX, targ.posY + 0.1, targ.posZ);
        setMotion(targ, VecUtils.multiply(delta, -1.2f));
    }

    private int[] ran(int a) {
        return new int[] { a - 3, a + 3 };
    }
}
