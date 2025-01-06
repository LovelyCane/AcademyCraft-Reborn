package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JetEngine extends Skill {
    public static final JetEngine INSTANCE = new JetEngine();
    public static final String SKILL_ID = "jet_engine";
    public static final int SKILL_LEVEL = 4;

    public JetEngine() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, JEContext::new);
    }
}
