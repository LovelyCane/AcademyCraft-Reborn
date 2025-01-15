package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
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
