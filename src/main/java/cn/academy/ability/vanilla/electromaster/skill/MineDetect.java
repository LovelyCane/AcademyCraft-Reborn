package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MineDetect extends Skill {
    public static final MineDetect INSTANCE = new MineDetect();
    public static final int TIME = 100;

    public MineDetect() {
        super("mine_detect", 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, MDContext::new);
    }
}
