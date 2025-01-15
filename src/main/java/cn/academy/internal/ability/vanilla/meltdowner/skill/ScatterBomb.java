package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ScatterBomb extends Skill {
    public static final ScatterBomb INSTANCE = new ScatterBomb();

    private ScatterBomb() {
        super("scatter_bomb", 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyId) {
        activateSingleKey(rt, keyId, SBContext::new);
    }
}
