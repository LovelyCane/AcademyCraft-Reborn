package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.lambdalib2.util.MathUtils.lerp;
import static cn.lambdalib2.util.MathUtils.lerpf;

public class ThunderClap extends Skill {
    public static final ThunderClap INSTANCE = new ThunderClap();
    public static final String SKILL_ID = "thunder_clap";
    public static final int SKILL_LEVEL = 5;

    public static final int MIN_TICKS = 40;
    public static final int MAX_TICKS = 60;

    public ThunderClap() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    public static float getDamage(float exp, int ticks) {
        return (float) (lerpf(36, 72, exp) * lerp(1.0f, 1.2f, (ticks - 40.0f) / 60.0f));
    }

    public static float getRange(float exp) {
        return lerpf(15, 30, exp);
    }

    public static int getCooldown(float exp, int ticks) {
        return (int) (ticks * lerp(10, 6, exp));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, ThunderClapContext::new);
    }
}
