package cn.academy.internal.terminal.app;

import cn.academy.internal.client.ui.MusicPlayerGui;
import cn.academy.internal.terminal.App;
import cn.academy.internal.terminal.AppEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class MusicApp extends App {
    public MusicApp() {
        super("media_player");
    }

    @SideOnly(Side.CLIENT)
    public AppEnvironment createEnvironment() {
        return new AppEnvironment() {
            @Override
            public void onStart() {
                Minecraft.getMinecraft().displayGuiScreen(new MusicPlayerGui());
            }
        };
    }
}
