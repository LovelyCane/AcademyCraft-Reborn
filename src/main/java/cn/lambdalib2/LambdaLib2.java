package cn.lambdalib2;

import cn.lambdalib2.multiblock.MsgBlockMulti;
import cn.lambdalib2.registry.RegistryMod;
import cn.lambdalib2.registry.impl.RegistryManager;
import cn.lambdalib2.registry.impl.RegistryTransformer;
import cn.lambdalib2.s11n.network.NetworkEvent;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.DebugDraw;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@RegistryMod(resourceDomain = "lambdalib2")
@Mod(modid = LambdaLib2.MODID, version = LambdaLib2.VERSION)
public class LambdaLib2
{
    public static final String MODID = "lambdalib2";
    public static final String VERSION = "@LAMBDA_LIB_VERSION@";

    /**
     * Whether we are in development (debug) mode.
     */
    public static final boolean DEBUG = VERSION.startsWith("@");

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
        channel.registerMessage(MsgBlockMulti.ReqHandler.class, MsgBlockMulti.Req.class, 4, Side.SERVER);
        channel.registerMessage(MsgBlockMulti.Handler.class, MsgBlockMulti.class, 5, Side.CLIENT);

        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if(DEBUG) log.info("LambdaLib2 is running in development mode.");
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DebugDraw());
    }

    // RegistryManager Hooks
    // In actual environment, LambdaLib2 is loaded before RegistryTransformer, so it's not transformed. LL2 mod is treated specially.
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        RegistryManager.asm_RegistrationEvent(this, event);
    }

}
