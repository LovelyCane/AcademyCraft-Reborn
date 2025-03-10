package cn.academy.internal.block;

import cn.academy.internal.inventory.ContainerPhaseGen;
import cn.academy.internal.tileentity.TilePhaseGen;
import cn.academy.internal.energy.client.ui.GuiPhaseGen;
import cn.lambdalib2.registry.mc.gui.GuiHandlerBase;
import cn.lambdalib2.registry.mc.gui.RegGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

public class BlockPhaseGen extends ACBlockContainer {
    @RegGuiHandler
    public static GuiHandlerBase guiHandler = new GuiHandlerBase() {
        @SideOnly(Side.CLIENT)
        @Override
        protected Object getClientContainer(EntityPlayer player, World world, int x, int y, int z) {
            ContainerPhaseGen container = (ContainerPhaseGen) getServerContainer(player, world, x, y, z);
            return GuiPhaseGen.apply(container);
        }

        @Override
        protected Object getServerContainer(EntityPlayer player, World world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            return te instanceof TilePhaseGen ? new ContainerPhaseGen(player, (TilePhaseGen) te) : null;
        }
    };

    public BlockPhaseGen() {
        super(Material.ROCK, guiHandler);
        setHardness(2.5f);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePhaseGen();
    }
}