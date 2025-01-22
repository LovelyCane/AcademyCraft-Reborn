package cn.academy.internal.client.renderer.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IHandRenderer {
    void renderHand(float partialTicks);
}
