package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Groundshock extends Skill {
    public static final Groundshock INSTANCE = new Groundshock();

    private Groundshock() {
        super("ground_shock", 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, GroundshockContext::new);
    }
}
