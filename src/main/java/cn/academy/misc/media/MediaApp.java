package cn.academy.misc.media;

import cn.academy.terminal.App;
import cn.academy.terminal.AppEnvironment;
import cn.academy.terminal.AppRegistry;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MediaApp extends App {
    public MediaApp() {
        super("media_player");
    }

    @StateEventCallback
    private static void init(FMLPreInitializationEvent event) {
        AppRegistry.register(new MediaApp());
    }

    @SideOnly(Side.CLIENT)
    public AppEnvironment createEnvironment() {
        return new AppEnvironment() {
            @Override
            public void onStart() {
                Minecraft.getMinecraft().displayGuiScreen(new MediaGui());
            }
        };
    }
}
