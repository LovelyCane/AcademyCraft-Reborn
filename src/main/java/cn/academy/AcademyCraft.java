package cn.academy;

import cn.lambdalib2.crafting.RecipeRegistry;
import cn.lambdalib2.registry.RegistryMod;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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
    @Instance("academy-craft")
    public static AcademyCraft INSTANCE;
    public static final boolean DEBUG_MODE = false;
    public static final Logger log = LogManager.getLogger("AcademyCraft");
    public static Configuration config;
    private static final String configFilePath = Minecraft.getMinecraft().gameDir.getPath() + File.separator + "config" + File.separator + Tags.MOD_ID + ".json";
    public static AcademyCraftConfig academyCraftConfig;
    public static RecipeRegistry recipes;

    static {
        try {
            academyCraftConfig = AcademyCraftConfig.loadConfig(configFilePath);
            AcademyCraft.log.info("Loaded AcademyCraft config");
        } catch (IOException e) {
            AcademyCraft.log.error("Failed to load config file");
        }
    }

    public static CreativeTabs cct = new CreativeTabs("AcademyCraft") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ACItems.logo);
        }
    };

    @StateEventCallback(priority = 1)
    @SuppressWarnings("unused")
    private static void preInit(FMLPreInitializationEvent event) {
        log.info("Starting AcademyCraft");
        log.info("Copyright (c) Lambda Innovation, 2013-2018");

        OBJLoader.INSTANCE.addDomain(Tags.MOD_ID);
        recipes = new RecipeRegistry();

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        OreDictionary.registerOre("plateIron", ACItems.reinforced_iron_plate);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        recipes.addRecipeFromResourceLocation(new ResourceLocation("academy:recipes/default.recipe"));
        recipes = null;
        config.save();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ACConfig.updateConfig(null);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        config.save();
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(ClientDisconnectionFromServerEvent e) {
        config.save();
    }
}