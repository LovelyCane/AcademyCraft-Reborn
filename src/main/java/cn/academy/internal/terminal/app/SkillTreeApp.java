package cn.academy.internal.terminal.app;

import cn.academy.internal.client.ui.SkillTreeAppUI;
import cn.academy.internal.terminal.App;
import cn.academy.internal.terminal.AppEnvironment;
import cn.academy.internal.terminal.RegApp;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

@SuppressWarnings("unused")
public class SkillTreeApp extends App {
    @RegApp
    public static SkillTreeApp instance = new SkillTreeApp();

    public SkillTreeApp() {
        super("skill_tree");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AppEnvironment createEnvironment() {
        return new AppEnvironment() {
            @SideOnly(Side.CLIENT)
            @Override
            public void onStart() {
                Minecraft.getMinecraft().displayGuiScreen(SkillTreeAppUI.apply());
            }
        };
    }
}