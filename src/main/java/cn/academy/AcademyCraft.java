package cn.academy;

import cn.academy.internal.ability.CategoryManager;
import cn.academy.internal.ability.Controllable;
import cn.academy.internal.ability.ctrl.ClientHandler;
import cn.academy.internal.ability.vanilla.VanillaCategories;
import cn.academy.internal.ability.vanilla.electromaster.CatElectromaster;
import cn.academy.internal.ability.vanilla.meltdowner.skill.ElectronBomb;
import cn.academy.internal.ability.vanilla.meltdowner.skill.SBNetDelegate;
import cn.academy.internal.event.AcademyCraftEventManager;
import cn.lambdalib2.auxgui.AuxGuiHandler;
import cn.lambdalib2.registry.RegistryMod;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
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
        ElectronBomb.EffectDelegate.preInit();

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(Tags.MOD_ID);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        AcademyCraftRegister.registerAllDuringInit();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            AuxGuiHandler.init();
            ClientHandler.init();
        }
        OreDictionary.registerOre("plateIron", AcademyCraftItemList.REINFORCED_IRON_PLATE);
        Controllable.init();
        VanillaCategories.init();
        SBNetDelegate.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        CategoryManager.postInit();
        CatElectromaster.postInit();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        for (ICommand command : AcademyCraftCommandList.COMMAND_LIST) {
            event.registerServerCommand(command);
        }
    }
}