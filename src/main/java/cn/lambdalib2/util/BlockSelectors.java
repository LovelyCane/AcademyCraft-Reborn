/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class BlockSelectors {
    
    public static final IBlockSelector

    filNothing = (world, x, y, z, block) -> block != Blocks.AIR,

    filNormal = (world, x, y, z, block) -> {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        return state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB  &&
                b.canCollideCheck(state, false);
    },
    
    filEverything = (world, x, y, z, block) -> false,

    filReplacable = (world, x, y, z, block) -> !block.isReplaceable(world, new BlockPos(x, y, z));

}
