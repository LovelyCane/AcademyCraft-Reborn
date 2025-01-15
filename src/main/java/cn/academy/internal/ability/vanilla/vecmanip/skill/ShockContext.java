package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import static cn.lambdalib2.util.MathUtils.*;
import static cn.lambdalib2.util.VecUtils.*;

@SuppressWarnings("unused")
public class ShockContext extends Context<DirectedShock> {
    public static final String MSG_PERFORM = "perform";
    public static final String MSG_GENERATE_EFFECT = "gen_eff";

    private static final int MIN_TICKS = 6;
    private static final int MAX_ACCEPTED_TICKS = 50;
    private static final int MAX_TOLERANT_TICKS = 200;
    private static final int PUNCH_ANIM_TICKS = 6;

    private int ticker = 0;
    private boolean punched = false;
    private int punchTicker = 0;

    public ShockContext(EntityPlayer p) {
        super(p, DirectedShock.INSTANCE);
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
        sendToClient(MSG_PERFORM, ticks);

        if (consume()) {
            RayTraceResult trace = Raytrace.traceLiving(player, 3, EntitySelectors.living());
            if (trace.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entity = trace.entityHit;
                ctx.attack(entity, damage);
                knockback(entity);
                ctx.setCooldown((int) lerpf(60, 20, ctx.getSkillExp()));
                sendToClient(MSG_GENERATE_EFFECT, entity);

                Vec3d delta = multiply(subtract(entity.getPositionVector(), player.getPositionVector()).normalize(), 0.24);
                entity.motionX += delta.x;
                entity.motionY += delta.y;
                entity.motionZ += delta.z;

                ctx.addSkillExp(0.0035f);
            } else {
                ctx.addSkillExp(0.0010f);
            }
        }

        terminate();
    }

    @Listener(channel = MSG_GENERATE_EFFECT, side = Side.CLIENT)
    public void c_effect(Entity ent) {
        knockback(ent);
        punched = true;
    }

    private boolean consume() {
        float cp = lerpf(50, 100, ctx.getSkillExp());
        float overload = lerpf(18, 12, ctx.getSkillExp());
        return ctx.consume(overload, cp);
    }

    private final float damage = lerpf(7, 15, ctx.getSkillExp());

    private void knockback(Entity targ) {
        if (ctx.getSkillExp() >= 0.25f) {
            Vec3d delta = subtract(entityHeadPos(player), entityHeadPos(targ));
            delta = delta.normalize();
            delta = new Vec3d(delta.x, delta.y - 0.6f, delta.z).normalize();

            targ.setPosition(targ.posX, targ.posY + 0.1, targ.posZ);
            targ.motionX = delta.x * -0.7f;
            targ.motionY = delta.y * -0.7f;
            targ.motionZ = delta.z * -0.7f;
        }
    }
}
