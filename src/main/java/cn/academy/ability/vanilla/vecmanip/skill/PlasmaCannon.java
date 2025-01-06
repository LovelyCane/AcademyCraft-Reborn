package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlasmaCannon extends Skill {
    public static final PlasmaCannon INSTANCE = new PlasmaCannon();

    private PlasmaCannon() {
        super("plasma_cannon", 5);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, PlasmaCannonContext::new);
    }
}
