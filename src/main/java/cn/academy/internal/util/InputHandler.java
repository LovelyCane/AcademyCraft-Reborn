package cn.academy.internal.util;

import net.minecraftforge.common.MinecraftForge;

public class InputHandler {
    public static final InputHandler INSTANCE = new InputHandler();
    private InputHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}