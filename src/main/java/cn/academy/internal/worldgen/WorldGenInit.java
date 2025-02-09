package cn.academy.internal.worldgen;

import cn.academy.AcademyCraft;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This is the main registry of all the crafting materials. Ores name and
 * Recipe script names are all provided here.
 *
 * @author WeAthFolD, Shielian, KS
 */
public class WorldGenInit {
    public static boolean GENERATE_ORES = AcademyCraft.academyCraftConfig.getGeneric().isGenOres();
    public static boolean GENERATE_PHASE_LIQUID = AcademyCraft.academyCraftConfig.getGeneric().isGenPhaseLiquid();
    public static String[] GENERATE_ORES_BLACK_LIST = AcademyCraft.academyCraftConfig.getGeneric().getGenerateOresBlackList();

    public static void preInit() {
        GameRegistry.registerWorldGenerator(worldGen, 2);
    }

    public static final ACWorldGen worldGen = new ACWorldGen();
}