package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CurrentCharging extends Skill {
    public static final CurrentCharging INSTANCE = new CurrentCharging();

    private CurrentCharging() {
        super("charging", 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, ChargingContext::new);
    }
}
