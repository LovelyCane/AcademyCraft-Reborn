package cn.lambdalib2.multiblock;

import java.util.List;

import cn.lambdalib2.multiblock.BlockMulti.SubBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @author WeathFolD
 */
public class ItemBlockMulti extends ItemBlock {

    public static final PropertyInteger PROP_SUBID = PropertyInteger.create("subid", 0, 15);

    /**
     * @param block
     */
    public ItemBlockMulti(Block block) {
        super(block);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (!block.isReplaceable(world, pos)) {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && world.mayPlace(this.block, pos, false, facing, null))
        {
            // Further validation with BlockMulti logic
            // - begin patched code
            BlockMulti bm = (BlockMulti) this.block;
            int l = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            List<SubBlockPos> list = bm.buffer[bm.getRotation(l).ordinal()];
            for (SubBlockPos s : list) {
                BlockPos npos = pos.add(s.dx, s.dy, s.dz);
                Block t = world.getBlockState(npos).getBlock();
                if (!t.isReplaceable(world, npos)) {
                    return EnumActionResult.FAIL;
                }
            }
            // - End patched code
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, i, player, hand);
            if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                iblockstate1 = world.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
