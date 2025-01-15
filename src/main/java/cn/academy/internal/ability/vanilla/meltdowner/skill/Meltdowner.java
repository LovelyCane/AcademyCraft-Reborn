package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Meltdowner extends Skill {
    public static final Meltdowner INSTANCE = new Meltdowner();
    public static final String SKILL_ID = "meltdowner";
    public static final int SKILL_LEVEL = 3;

    public Meltdowner() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyId) {
        activateSingleKey(rt, keyId, MDContext::new);
    }
}