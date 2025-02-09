package cn.academy.internal.support.ic2;

import net.minecraftforge.common.MinecraftForge;

public class IC2Support {
    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(IC2SupportImpl.class);
    }
}
