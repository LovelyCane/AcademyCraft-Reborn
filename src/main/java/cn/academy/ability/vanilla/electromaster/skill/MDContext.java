package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.MathUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

@SuppressWarnings("unused")
public class MDContext extends Context<MineDetect> {
    private final float range = MathUtils.lerpf(15, 30, ctx.getSkillExp());
    private final boolean isAdvanced = ctx.getSkillExp() > 0.5f && ctx.aData.getLevel() >= 4;

    public MDContext(EntityPlayer p) {
        super(p, MineDetect.INSTANCE);  // Assuming MineDetect is an instance
    }

    private boolean consume() {
        float exp = ctx.getSkillExp();

        float cp = MathUtils.lerpf(1500, 1000, exp);
        float overload = MathUtils.lerpf(200, 180, exp);

        return ctx.consume(overload, cp);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_onKeyDown() {
        sendToServer(MDContext.MSG_EXECUTE);
    }

    @Listener(channel = MDContext.MSG_EXECUTE, side = Side.SERVER)
    private void s_execute() {
        if (consume()) {
            player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("blindness")), MineDetect.TIME));
            ctx.addSkillExp(0.008f);
            sendToClient(MDContext.MSG_EFFECT, range, isAdvanced);

            float exp = ctx.getSkillExp();
            int cooldown = (int) MathUtils.lerpf(900, 400, exp);
            ctx.setCooldown(cooldown);
        }
        terminate();
    }

    // Static Constants
    public static final String MSG_EFFECT = "effect";
    public static final String MSG_EXECUTE = "execute";
}
