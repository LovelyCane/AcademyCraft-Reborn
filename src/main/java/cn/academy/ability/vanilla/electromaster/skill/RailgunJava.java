package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.Skill;
import net.minecraftforge.common.MinecraftForge;

public class RailgunJava extends Skill {
    public static final RailgunJava INSTANCE = new RailgunJava();
    private RailgunJava() {
        super("railgun", 4);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }
}
