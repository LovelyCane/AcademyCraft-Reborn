package cn.academy.internal.client.ui;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.develop.DevelopData;
import cn.academy.internal.ability.develop.IDeveloper;
import cn.academy.internal.ability.develop.action.DevelopActionLevel;
import cn.academy.internal.ability.develop.action.DevelopActionReset;
import cn.academy.internal.ability.develop.action.DevelopActionSkill;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class NetDelegateJava {
    public static final NetDelegateJava INSTANCE = new NetDelegateJava();
    public static final String MSG_START_SKILL = "start_skill";
    public static final String MSG_RESET = "reset";
    public static final String MSG_START_LEVEL = "start_level";

    @StateEventCallback
    public static void onInit(FMLInitializationEvent evt) {
        NetworkS11n.addDirectInstance(INSTANCE);
    }

    @Listener(channel = MSG_START_SKILL, side = Side.SERVER)
    private void handleStartSkill(DevelopData data, IDeveloper developer, Skill skill) {
        data.startDeveloping(developer, new DevelopActionSkill(skill));
    }

    @Listener(channel = MSG_START_LEVEL, side = Side.SERVER)
    private void handleStartLevel(DevelopData data, IDeveloper developer) {
        data.startDeveloping(developer, new DevelopActionLevel());
    }

    @Listener(channel = MSG_RESET, side = Side.SERVER)
    private void handleStartReset(DevelopData data, IDeveloper developer) {
        data.startDeveloping(developer, new DevelopActionReset());
    }
}