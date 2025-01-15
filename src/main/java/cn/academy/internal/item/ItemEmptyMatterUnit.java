package cn.academy.internal.item;

import cn.academy.AcademyCraftBlockList;
import cn.academy.AcademyCraftItemList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemEmptyMatterUnit extends ItemBucket {
    public ItemEmptyMatterUnit() {
        super(Blocks.AIR);
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        RayTraceResult rayRes = rayTrace(world, player, true);
        if (rayRes == null)
        {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        else if (rayRes.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        BlockPos pos = rayRes.getBlockPos();
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() != AcademyCraftBlockList.IMAG_PHASE) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!world.canMineBlockBody(player, pos) || !player.canPlayerEdit(pos, rayRes.sideHit, stack)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!world.isRemote) {
            world.setBlockToAir(pos);

            ItemStack newStack = new ItemStack(AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID, 1);
            if (!player.inventory.addItemStackToInventory(newStack)) {
                player.dropItem(newStack, false);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
