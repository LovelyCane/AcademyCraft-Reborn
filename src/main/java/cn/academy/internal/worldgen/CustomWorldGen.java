package cn.academy.internal.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * @author WeathFolD
 */

public class CustomWorldGen {
    IBlockState blockState;
    private WorldGenerator gen;
    int yLimit;
    int densityPerChunk;
    Biome[] biomeIds;

    public CustomWorldGen(IBlockState blockState, int num, int _yLimit, int _density, Biome... _biomeIds) {
        this.blockState = blockState;
        this.gen = new WorldGenMinable(blockState,num);
        yLimit = _yLimit;
        densityPerChunk = _density;
        biomeIds = _biomeIds;
    }

    public void generate(World world, Random rand, int chunkMinX, int chunkMinZ) {
        if (!canGen(world, chunkMinX, chunkMinZ))
            return;
        for (int i = 0; i < densityPerChunk; ++i) {
            int x = chunkMinX + rand.nextInt(16), y = rand.nextInt(yLimit), z = chunkMinZ + rand.nextInt(16);

            gen.generate(world, rand, new BlockPos(x, y, z));
        }
    }

    private boolean canGen(World world, int x, int z) {
        if (biomeIds.length == 0)
            return true;
        Biome chunkmgr = world.getBiome(new BlockPos(x, 0, z));
        for (Biome i : biomeIds)
            if (i == chunkmgr)
                return true;
        return false;
    }
}