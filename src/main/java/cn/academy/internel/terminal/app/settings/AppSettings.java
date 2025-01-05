package cn.academy.internel.terminal.app.settings;

import cn.academy.internel.terminal.App;
import cn.academy.internel.terminal.AppEnvironment;
import cn.academy.internel.terminal.RegApp;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

/**
 * @author WeAthFolD
 *
 */
public class AppSettings extends App {

    @RegApp(priority = -1)
    public static AppSettings instance = new AppSettings();
    
    private AppSettings() {
        super("settings");
        setPreInstalled();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AppEnvironment createEnvironment() {
        return new AppEnvironment() {
            @Override
            @SideOnly(Side.CLIENT)
            public void onStart() {
                Minecraft.getMinecraft().displayGuiScreen(new SettingsUI());
            }
        };
    }
}