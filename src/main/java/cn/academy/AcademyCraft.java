package cn.academy;

import cn.academy.entity.EntityMagHook;
import cn.lambdalib2.crafting.RecipeRegistry;
import cn.lambdalib2.registry.RegistryMod;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;
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

    public static AcademyConfig academyConfig;

    public static void test(Object node,Object tile,Object pass) {
        AcademyCraft.log.info("Node: " + node + "Tile: " + tile + "Pass" + pass);
    }
    static {
        try {
            academyConfig = AcademyConfig.loadConfig(configFilePath);
            AcademyCraft.log.info("Loaded AcademyCraft config");
        } catch (IOException e) {
            AcademyCraft.log.error("Failed to load config file");
        }
    }

    public static RecipeRegistry recipes;

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

        OBJLoader.INSTANCE.addDomain(Tags.MOD_ID);
        recipes = new RecipeRegistry();

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        OreDictionary.registerOre("plateIron", ACItems.reinforced_iron_plate);
        registerEntity("entitymaghook", EntityMagHook.class);
    }

    private void registerEntity(String name, Class<? extends Entity> entityClass) {
        ResourceLocation id = new ResourceLocation(Tags.MOD_ID, name);
        EntityRegistry.registerModEntity(
                id,                       // 实体的注册名
                entityClass,              // 实体类
                name,                     // 实体的名称
                1314,               // 实体的唯一ID（每个实体类型不同）
                this,                     // Mod 实例
                64,                       // 更新范围（通常为64）
                1,                        // 更新频率（tick间隔，1表示每tick更新）
                true                      // 是否追踪实体（true表示在客户端同步）
        );
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Load script, where names now are available
        recipes.addRecipeFromResourceLocation(new ResourceLocation("academy:recipes/default.recipe"));

        recipes = null; // Release and have fun GC
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
    public void onClientDisconnectionFromServer(
            ClientDisconnectionFromServerEvent e) {
        config.save();
    }

    /**
     * Simply a fast route to print debug message.
     */
    public static void debug(Object msg) {
        if (DEBUG_MODE) {
            log.info(msg);
        }
    }
}