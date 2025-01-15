package cn.academy.internal.ability.vanilla.meltdowner.skill;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BasicMRContext extends MRContext {
    public BasicMRContext(EntityPlayer p) {
        super(p, MineRayBasic.INSTANCE);
        setRange(10);
        setHarvestLevel(2);
        setSpeed(0.2f, 0.4f);
        setConsumption(12.0f, 7.0f);
        setOverload(200f, 150f);
        setCooldown(40f, 20f);
        setExpIncr(0.0005f);
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, Block block) {
        // Get block state and play sound
        Block blockState = world.getBlockState(pos).getBlock();
        SoundEvent sound = blockState.getSoundType(world.getBlockState(pos), world, pos, player).getBreakSound();
        world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, 0.5f, 1f, false);
        block.dropBlockAsItemWithChance(world, pos, world.getBlockState(pos), 1.0f, 0);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
