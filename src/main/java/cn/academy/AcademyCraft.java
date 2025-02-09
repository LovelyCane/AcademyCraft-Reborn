package cn.academy;

import cn.academy.internal.ability.CategoryManager;
import cn.academy.internal.ability.Controllable;
import cn.academy.internal.ability.context.RegClientContextImpl;
import cn.academy.internal.ability.ctrl.ClientHandler;
import cn.academy.internal.ability.vanilla.VanillaCategories;
import cn.academy.internal.ability.vanilla.electromaster.CatElectromaster;
import cn.academy.internal.ability.vanilla.meltdowner.skill.ElectronBomb;
import cn.academy.internal.ability.vanilla.meltdowner.skill.SBNetDelegate;
import cn.academy.internal.advancements.ACAdvancements;
import cn.academy.internal.client.ui.NotifyUI;
import cn.academy.internal.crafting.MFIFRecipes;
import cn.academy.internal.entity.EntitySilbarn;
import cn.academy.internal.event.AcademyCraftEventManager;
import cn.academy.internal.support.ic2.IC2Support;
import cn.academy.internal.support.rf.RFSupport;
import cn.academy.internal.worldgen.PhaseLiquidGenerator;
import cn.academy.internal.worldgen.WorldGenInit;
import cn.lambdalib2.auxgui.AuxGuiHandler;
import cn.lambdalib2.datapart.CapDataPartHandler;
import cn.lambdalib2.datapart.RegDataPartImpl;
import cn.lambdalib2.registry.mc.gui.RegGuiHandlerImpl;
import cn.lambdalib2.s11n.network.FutureManager;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.util.ControlOverrider;
import cn.lambdalib2.util.ReflectionUtils;
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

    public static final CreativeTabs CREATIVE_TABS = new CreativeTabs("AcademyCraft") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(AcademyCraftItemList.LOGO);
        }
    };

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Starting AcademyCraft");
        LOGGER.info("Copyright (c) Lambda Innovation, 2013-2018");

        ReflectionUtils._init(event.getAsmData());

        AcademyCraftEventManager.registerEventBus();
        AcademyCraftRegister.registerAllDuringPreInit();
        RegDataPartImpl.init();
        ElectronBomb.EffectDelegate.preInit();

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(Tags.MOD_ID);
            EntitySilbarn.init();
        }

        NetworkS11n.preInit();
        PhaseLiquidGenerator.preInit();
        CapDataPartHandler.register();
        WorldGenInit.preInit();
        RFSupport.preInit();
        IC2Support.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        AcademyCraftRegister.registerAllDuringInit();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            AuxGuiHandler.init();
            ClientHandler.init();
            SBNetDelegate.init();
            NotifyUI.init();
            ControlOverrider.init();
            RegClientContextImpl.init();
        }
        OreDictionary.registerOre("plateIron", AcademyCraftItemList.REINFORCED_IRON_PLATE);
        Controllable.init();
        VanillaCategories.init();
        ACAdvancements.init();
        FutureManager.instance.init();
        MFIFRecipes.init();
        RegGuiHandlerImpl.init();
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