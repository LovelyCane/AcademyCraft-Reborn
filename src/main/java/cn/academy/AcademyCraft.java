package cn.academy;

import cn.academy.analytic.AnalyticDataListener;
import cn.lambdalib2.crafting.RecipeRegistry;
import cn.lambdalib2.registry.RegistryMod;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
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

/**
 * Academy Craft Mod Main Class
 *
 * @author acaly, WeathFolD, KS
 */

@Mod(modid = "academy", name = "AcademyCraft", version = AcademyCraft.VERSION, dependencies = "required-after:lambdalib2@@LAMBDA_LIB_VERSION@")
@RegistryMod(rootPackage = "cn.academy.", resourceDomain = "academy")
public class AcademyCraft {
    @Instance("academy-craft")
    public static AcademyCraft INSTANCE;
    public static final String VERSION = Tags.VERSION;
    public static final boolean DEBUG_MODE = VERSION.startsWith("@");
    public static final Logger log = LogManager.getLogger(Tags.MOD_NAME);

    public static Configuration config;
    public static RecipeRegistry recipes;

    public static AnalyticDataListener analyticDataListener;

    public static CreativeTabs cct = new CreativeTabs("AcademyCraft") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ACItems.logo);
        }
    };

    @StateEventCallback(priority = 1)
    private static void preInit(FMLPreInitializationEvent event) {
        log.info("Starting AcademyCraft");
        log.info("Copyright (c) Lambda Innovation, 2013-2018");
        log.info("https://ac.li-dev.cn/");

        recipes = new RecipeRegistry();

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        if (config.getBoolean("analysis", "generic", true, "switch for analytic system")) {
            AnalyticDataListener analyticDataListener = AnalyticDataListener.instance;
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        OreDictionary.registerOre("plateIron", ACItems.reinforced_iron_plate);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        recipes = null; // Recipes are no longer needed
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

    /**
     * Debug message printing.
     */
    public static void debug(Object msg) {
        if (DEBUG_MODE) {
            log.info(msg);
        }
    }
}