package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ThunderBolt extends Skill {
    public static final ThunderBolt INSTANCE = new ThunderBolt();
    public static final String SKILL_ID = "thunder_bolt";
    public static final int SKILL_LEVEL = 4;

    public static final double RANGE = 20d;
    public static final double AOE_RANGE = 8d;

    public ThunderBolt() {
        super(SKILL_ID, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyId) {
        activateSingleKey(rt, keyId, ThunderBoltContext::new);
    }
}
