package cn.academy.internal.terminal.app.settings;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UIProperty {
    @SideOnly(Side.CLIENT)
    public static String getDisplayID(String name) {
        return I18n.format("ac.settings.prop." + name);
    }
}