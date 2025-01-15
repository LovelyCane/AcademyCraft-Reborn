package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;

import static cn.academy.internal.ability.vanilla.electromaster.skill.ThunderClap.getCooldown;
import static cn.academy.internal.ability.vanilla.electromaster.skill.ThunderClap.getDamage;

@SuppressWarnings("unused")
public class ThunderClapContext extends Context<ThunderClap> {
    private static final String MSG_START = "start";
    private static final String MSG_END = "end";
    private static final String MSG_EFFECT_START = "effect_start";

    private final float exp = ctx.getSkillExp();
    private int ticks = 0;
    private double hitX, hitY, hitZ = 0d;

    public ThunderClapContext(EntityPlayer p) {
        super(p, ThunderClap.INSTANCE);
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_onKeyDown() {
        sendToServer(MSG_START);
    }

    @NetworkMessage.Listener(channel = MSG_START, side = Side.SERVER)
    private void s_onStart() {
        sendToClient(MSG_EFFECT_START);

        float overload = MathUtils.lerpf(390, 252, exp);
        ctx.consume(overload, 0);
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        final double DISTANCE = 40.0;
        RayTraceResult pos = Raytrace.traceLiving(player, DISTANCE, EntitySelectors.nothing());
        hitX = pos.hitVec.x;
        hitY = pos.hitVec.y;
        hitZ = pos.hitVec.z;

        ticks++;

        float consumption = MathUtils.lerpf(18, 25, exp);
        if ((ticks <= ThunderClap.MIN_TICKS && !ctx.consume(0, consumption)) || ticks >= ThunderClap.MAX_TICKS) {
            sendToSelf(MSG_END);
        }
    }

    @NetworkMessage.Listener(channel = MSG_END, side = Side.SERVER)
    private void s_onEnd() {
        if (ticks < ThunderClap.MIN_TICKS) {
            terminate();
            return;
        }

        EntityLightningBolt lightning = new EntityLightningBolt(player.world, hitX, hitY, hitZ, true);
        player.getEntityWorld().addWeatherEffect(lightning);
        ctx.attackRange(hitX, hitY, hitZ, ThunderClap.getRange(exp), getDamage(exp, ticks), EntitySelectors.exclude(player));

        ctx.setCooldown(getCooldown(exp, ticks));
        ctx.addSkillExp(0.003f);
        terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onEnd() {
        terminate();
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        terminate();
    }
}
