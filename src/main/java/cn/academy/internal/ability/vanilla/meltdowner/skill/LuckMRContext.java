package cn.academy.internal.ability.vanilla.meltdowner.skill;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LuckMRContext extends MRContext {
    EntityPlayer player;

    public LuckMRContext(EntityPlayer p) {
        super(p, MineRayLuck.INSTANCE); // Assuming MineRayLuck is a singleton
        this.player = p;
        setRange(20);
        setHarvestLevel(5);
        setSpeed(0.5f, 1);
        setConsumption(50, 35);
        setOverload(350, 300);
        setCooldown(60, 30);
        setExpIncr(0.0003f);
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, Block block) {
        // Get the block state and its break sound
        net.minecraft.block.state.IBlockState blockState = world.getBlockState(pos);
        SoundEvent sound = block.getSoundType(blockState, world, pos, player).getBreakSound();

        // Play the block break sound at the given position
        world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, 0.5f, 1f, false);

        // Drop the block as an item with a chance of 1.0f and a fortune level of 3
        block.dropBlockAsItemWithChance(world, pos, blockState, 1.0f, 3);

        // Set the block to air after breaking
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}