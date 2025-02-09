package cn.lambdalib2;

import cn.lambdalib2.s11n.network.NetworkEvent;
import cn.lambdalib2.s11n.network.NetworkMessage;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = LambdaLib2.MODID, version = LambdaLib2.VERSION)
public class LambdaLib2 {
    public static final String MODID = "lambdalib2";
    public static final String VERSION = "0.2.1";

    /**
     * Whether we are in development (debug) mode.
     */
    public static final boolean DEBUG = false;

    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static Configuration config;

    private static Logger log;

    public static Logger getLogger() {
        return log;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        channel.registerMessage(NetworkEvent.MessageHandler.class, NetworkEvent.Message.class, 0, Side.CLIENT);
        channel.registerMessage(NetworkEvent.MessageHandler.class, NetworkEvent.Message.class, 1, Side.SERVER);
        channel.registerMessage(NetworkMessage.Handler.class, NetworkMessage.Message.class, 2, Side.CLIENT);
        channel.registerMessage(NetworkMessage.Handler.class, NetworkMessage.Message.class, 3, Side.SERVER);
    }
}