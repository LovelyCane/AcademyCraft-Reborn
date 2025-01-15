package cn.academy.internal.worldgen;

import cn.academy.AcademyCraftBlockList;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * AC Ore Generator
 *
 * @author KSkun
 */

public class ACWorldGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (WorldGenInit.GENERATE_ORES && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            genOverworld(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private List<CustomWorldGen> generators = Arrays.asList(new CustomWorldGen(new WorldGenMinable(AcademyCraftBlockList.RESO_ORE.getDefaultState(), 9), 60, 8), new CustomWorldGen(new WorldGenMinable(AcademyCraftBlockList.CONSTRAINT_METAL.getDefaultState(), 12), 60, 8), new CustomWorldGen(new WorldGenMinable(AcademyCraftBlockList.CRYSTAL_ORE.getDefaultState(), 12), 60, 12), new CustomWorldGen(new WorldGenMinable(AcademyCraftBlockList.IMAGSIL_ORE.getDefaultState(), 11), 60, 8));

    private void genOverworld(World world, Random random, int x, int z) {
        for (CustomWorldGen gen : generators) {
            gen.generate(world, random, x, z);
        }
    }
}