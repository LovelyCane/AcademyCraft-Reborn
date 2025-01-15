package cn.academy.internal.event;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftRegister;
import cn.academy.internal.client.misc.MusicSystem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AcademyCraftEventManager {
    public static void registerEventBus() {
        MinecraftForge.EVENT_BUS.register(AcademyCraft.class);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(MusicSystem.class);
        }
        MinecraftForge.EVENT_BUS.register(AcademyCraftRegister.class);
    }
}
