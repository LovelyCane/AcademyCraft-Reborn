package cn.academy.internal.block;

import cn.academy.internal.energy.client.ui.GuiSolarGen;
import cn.academy.internal.inventory.ContainerSolarGen;
import cn.academy.internal.tileentity.TileSolarGen;
import cn.lambdalib2.registry.mc.gui.GuiHandlerBase;
import cn.lambdalib2.registry.mc.gui.RegGuiHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

public class BlockSolarGen extends BlockContainer {
    @RegGuiHandler
    public static final GuiHandlerBase handler = new GuiHandlerBase() {
        @SideOnly(Side.CLIENT)
        @Override
        protected Object getClientContainer(EntityPlayer player, World world, int x, int y, int z) {
            ContainerSolarGen container = ((ContainerSolarGen) getServerContainer(player, world, x, y, z));
            return GuiSolarGen.apply(container);
        }

        @Override
        protected Object getServerContainer(EntityPlayer player, World world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileSolarGen) {
                return new ContainerSolarGen(player, ((TileSolarGen) te));
            } else {
                return null;
            }
        }
    };

    public BlockSolarGen() {
        super(Material.ROCK);

        setHardness(1.5f);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSolarGen();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (handler != null && !player.isSneaking()) {
            if (!world.isRemote)
                handler.openGuiContainer(player, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }
}