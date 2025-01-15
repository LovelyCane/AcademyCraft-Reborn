package cn.academy.internal.worldgen;

import cn.academy.AcademyCraft;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This is the main registry of all the crafting materials. Ores name and
 * Recipe script names are all provided here.
 *
 * @author WeAthFolD, Shielian, KS
 */
public class WorldGenInit {
    public static boolean GENERATE_ORES, GENERATE_PHASE_LIQUID;
    public static String[] GENERATE_ORES_BLACK_LIST;

    @StateEventCallback
    public static void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(worldGen, 2);
    }

    public static final ACWorldGen worldGen = new ACWorldGen();

    @StateEventCallback
    private static void init(FMLInitializationEvent event) {
        GENERATE_ORES = AcademyCraft.academyCraftConfig.getGeneric().isGenOres();
        GENERATE_PHASE_LIQUID = AcademyCraft.academyCraftConfig.getGeneric().isGenPhaseLiquid();
        GENERATE_ORES_BLACK_LIST = AcademyCraft.academyCraftConfig.getGeneric().getGenerateOresBlackList();
    }
}