package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BloodRetrograde extends Skill {
    public static final BloodRetrograde INSTANCE = new BloodRetrograde();
    public static final String SKILL_ID = "blood_retro";
    public static final int SKILL_LEVEL = 4;

    public BloodRetrograde() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, BloodRetroContext::new);
    }
}
