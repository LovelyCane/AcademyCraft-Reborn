package cn.academy.internal.terminal.app;

import cn.academy.internal.client.ui.MusicPlayerGui;
import cn.academy.internal.terminal.App;
import cn.academy.internal.terminal.AppEnvironment;
import cn.academy.internal.terminal.AppRegistry;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class MusicApp extends App {
    public MusicApp() {
        super("media_player");
    }

    @StateEventCallback
    private static void init(FMLPreInitializationEvent event) {
        AppRegistry.register(new MusicApp());
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
