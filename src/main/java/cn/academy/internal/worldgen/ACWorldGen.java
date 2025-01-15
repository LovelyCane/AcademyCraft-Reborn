package cn.academy.internal.worldgen;

import cn.academy.AcademyCraftBlockList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AC Ore Generator
 *
 * @author KSkun
 */
public class ACWorldGen implements IWorldGenerator {
    private final List<CustomWorldGen> generators = Arrays.asList(new CustomWorldGen(AcademyCraftBlockList.RESO_ORE.getDefaultState(), 9, 60, 8), new CustomWorldGen(AcademyCraftBlockList.CONSTRAINT_METAL.getDefaultState(), 12, 60, 8), new CustomWorldGen(AcademyCraftBlockList.CRYSTAL_ORE.getDefaultState(), 12, 60, 12), new CustomWorldGen(AcademyCraftBlockList.IMAGSIL_ORE.getDefaultState(), 11, 60, 8));

    private List<IBlockState> cachedFinalBlockList;
    private List<CustomWorldGen> cachedFinalGenerators;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (WorldGenInit.GENERATE_ORES && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            genOverworld(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private List<IBlockState> finalBlockList() {
        if (cachedFinalBlockList == null) {
            Set<String> blacklistedNames = new HashSet<>(Arrays.asList(WorldGenInit.GENERATE_ORES_BLACK_LIST));
            cachedFinalBlockList = AcademyCraftBlockList.BLOCK_LIST.stream().filter(block -> !blacklistedNames.contains(block.getRegistryName().getPath())).map(Block::getDefaultState).collect(Collectors.toList());
        }
        return cachedFinalBlockList;
    }

    private List<CustomWorldGen> finalGenerators() {
        if (cachedFinalGenerators == null) {
            Set<IBlockState> allowedBlocks = new HashSet<>(finalBlockList());
            cachedFinalGenerators = generators.stream().filter(gen -> allowedBlocks.contains(gen.blockState)).collect(Collectors.toList());
        }
        return cachedFinalGenerators;
    }

    private void genOverworld(World world, Random random, int x, int z) {
        for (CustomWorldGen gen : finalGenerators()) {
            gen.generate(world, random, x, z);
        }
    }
}
