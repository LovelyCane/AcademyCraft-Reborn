package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DirectedBlastwave extends Skill {
    public static final DirectedBlastwave INSTANCE = new DirectedBlastwave();

    public DirectedBlastwave() {
        super("dir_blast", 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, BlastwaveContext::new);
    }
}
