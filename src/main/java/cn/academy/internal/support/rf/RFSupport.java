package cn.academy.internal.support.rf;

import net.minecraftforge.common.MinecraftForge;

public class RFSupport {
    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(RFSupportImpl.class);
    }
}