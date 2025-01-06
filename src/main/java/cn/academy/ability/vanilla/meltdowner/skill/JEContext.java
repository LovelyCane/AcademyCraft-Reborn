package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import static cn.lambdalib2.util.MathUtils.lerpf;

@SuppressWarnings("unused")
public class JEContext extends Context<JetEngine> {
    public static final String MSG_TRIGGER = "trigger";
    public static final String MSG_MARK_END = "mark_end";

    private final float exp = ctx.getSkillExp();
    private final float consumption = lerpf(170, 140, exp);
    private final float overload = lerpf(60, 50, exp);

    private static final float TIME = 8;
    private static final float LIFETIME = 15;

    private Vec3d target;
    private int ticks = 0;
    private boolean isTriggering = false;

    private Vec3d start;
    private Vec3d velocity;

    public JEContext(EntityPlayer p) {
        super(p, JetEngine.INSTANCE);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if (!ctx.canConsumeCP(consumption))
            terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onKeyUp() {
        sendToServer(MSG_MARK_END);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onKeyAbort() {
        sendToSelf(MSG_MARK_END);
        terminate();
    }

    @Listener(channel = MSG_MARK_END, side = Side.SERVER)
    private void s_onEnd() {
        if (ctx.consume(consumption, overload)) {
            sendToClient(MSG_MARK_END);
            sendToSelf(MSG_TRIGGER, getDest());
            ctx.addSkillExp(0.004f);
            ctx.setCooldown((int) lerpf(60, 30, exp));
        } else {
            sendToClient(MSG_MARK_END);
            terminate();
        }
    }

    private Vec3d getDest() {
        Pair<Vec3d, RayTraceResult> result = Raytrace.getLookingPos(player, 12, EntitySelectors.nothing());
        return result.getLeft();
    }

    @Listener(channel = MSG_TRIGGER, side = Side.SERVER)
    private void s_triggerStart(Vec3d _target) {
        target = _target;
        isTriggering = true;
        sendToClient(MSG_TRIGGER, target);
    }

    @Listener(channel = MSG_TRIGGER, side = Side.CLIENT)
    private void c_triggerStart(Vec3d _target) {
        if (isLocal()) {
            isTriggering = true;
            target = _target;
            start = player.getPositionVector();
            velocity = VecUtils.multiply(VecUtils.subtract(target, start), 1.0 / TIME);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_triggerTick() {
        if (isTriggering) {
            RayTraceResult pos = Raytrace.perform(world(), new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ), new Vec3d(player.posX, player.posY, player.posZ), EntitySelectors.exclude(player).and(EntitySelectors.living()));
            if (player.getRidingEntity() != null)
                player.dismountRidingEntity();
            if (pos.entityHit != null) {
                MDDamageHelper.attack(ctx, pos.entityHit, lerpf(7, 20, exp));
            }
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_triggerTick() {
        if (isLocal() && isTriggering) {
            if (ticks >= LIFETIME)
                terminate();
            ticks++;
            Vec3d pos = VecUtils.lerp(start, target, ticks / TIME);
            player.setPosition(pos.x, pos.y, pos.z);
            VecUtils.setMotion(player, velocity);
            player.fallDistance = 0.0f;
        }
    }
}
