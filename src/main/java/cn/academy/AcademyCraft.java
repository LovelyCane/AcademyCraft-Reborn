package cn.academy;

import cn.academy.internal.event.AcademyCraftEventManager;
import cn.lambdalib2.auxgui.AuxGuiHandler;
import cn.lambdalib2.registry.RegistryMod;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Academy Craft Mod Main Class
 *
 * @author acaly, WeathFolD, KS
 */
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
@RegistryMod(rootPackage = Tags.ROOT_PACKAGE + ".", resourceDomain = Tags.MOD_ID)
public class AcademyCraft {
    @Instance("academy")
    public static AcademyCraft instance;
    public static final boolean DEBUG_MODE = false;
    public static final Logger LOGGER = LogManager.getLogger("AcademyCraft");
    public static Configuration config;
    public static AcademyCraftConfig academyCraftConfig;
    public static final File configFile;

    static {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            configFile = new File(".", "config" + File.separator + Tags.MOD_ID + ".json");
        } else {
            configFile = new File(Minecraft.getMinecraft().gameDir, "config" + File.separator + Tags.MOD_ID + ".json");
        }

        try {
            if (!configFile.exists()) {
                boolean created = configFile.createNewFile();
                if (created) {
                    AcademyCraft.LOGGER.info("Created new config file: {}", configFile.getAbsolutePath());
                }
            }
            academyCraftConfig = AcademyCraftConfig.loadConfig(configFile);
            AcademyCraft.LOGGER.info("Loaded AcademyCraft config");
        } catch (IOException e) {
            AcademyCraft.LOGGER.error("Failed to load or create config file: {}", e.getMessage());
        }
    }

    public static CreativeTabs cct = new CreativeTabs("AcademyCraft") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(AcademyCraftItemList.LOGO);
        }
    };

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Starting AcademyCraft");
        LOGGER.info("Copyright (c) Lambda Innovation, 2013-2018");

        AcademyCraftEventManager.registerEventBus();
        AcademyCraftRegister.registerAllDuringPreInit();

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(Tags.MOD_ID);
        }

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        OreDictionary.registerOre("plateIron", AcademyCraftItemList.REINFORCED_IRON_PLATE);
        AcademyCraftRegister.registerAllDuringInit();
        AuxGuiHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        config.save();
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        config.save();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        for (ICommand command : AcademyCraftCommandList.COMMAND_LIST) {
            event.registerServerCommand(command);
        }
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(ClientDisconnectionFromServerEvent e) {
        config.save();
    }
}