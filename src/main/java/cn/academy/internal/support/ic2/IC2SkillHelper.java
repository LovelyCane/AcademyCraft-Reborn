package cn.academy.internal.support.ic2;

import cn.academy.internal.entity.EntitySurroundArc;
import cn.academy.internal.event.ability.AbilityActivateEvent;
import cn.lambdalib2.util.IBlockSelector;
import cn.lambdalib2.util.WorldUtils;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KSkun
 */
public class IC2SkillHelper {
    private static final IBlockSelector blockSelector = (world, x, y, z, block) -> {
        if (block instanceof BlockTileEntity) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            return te instanceof TileEntityCable;
        }
        return false;
    };

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onAbilityActive(AbilityActivateEvent event) {
        spawnArc(event.player);
    }

    @SideOnly(Side.CLIENT)
    public void spawnArc(EntityPlayer player) {
        List<BlockPos> blockList = new ArrayList<>();
        WorldUtils.getBlocksWithin(blockList, player, 5, 100, blockSelector);
        World world = player.world;
        for (BlockPos bp : blockList) {
            world.spawnEntity(new EntitySurroundArc(world, bp.getX(), bp.getY(), bp.getZ(), 1, 1));
        }
    }
}