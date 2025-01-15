package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElectronMissile extends Skill {
    public static final ElectronMissile INSTANCE = new ElectronMissile();
    public static final String SKILL_ID = "electron_missile";
    public static final int SKILL_LEVEL = 5;

    public ElectronMissile() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyId) {
        activateSingleKey(rt, keyId, EMContext::new);
    }
}
