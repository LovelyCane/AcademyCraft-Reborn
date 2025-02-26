package cn.academy.internal.support;

import cn.academy.internal.block.ACBlockContainer;
import cn.academy.internal.client.ui.WirelessPage;
import cn.academy.internal.energy.api.block.IWirelessUser;
import cn.lambdalib2.cgui.CGuiScreen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

public abstract class BlockConverterBase extends ACBlockContainer {
    public static class Item extends ItemBlock {
        BlockConverterBase converter;

        public Item(Block block) {
            super(block);
            converter = (BlockConverterBase) block;
        }
    }

    public final Class<? extends TileEntity> tileType;
    public final String from, to;

    public BlockConverterBase(String _from, String _to, Class<? extends TileEntity> _tileType) {
        super(Material.ROCK);
        from = _from;
        to = _to;
        tileType = _tileType;
        setHarvestLevel("pickaxe", 0);
        setHardness(2.5f);
    }

    @Override
    @SuppressWarnings("sideonly")
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && tileType.isInstance(te)) {
            if (te instanceof IWirelessUser && !player.isSneaking()) {
                if (world.isRemote) {
                    displayGui((IWirelessUser) te);
                }
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void displayGui(IWirelessUser te) {
        CGuiScreen screen = new CGuiScreen() {
            @Override
            public boolean doesGuiPauseGame() {
                return false;
            }
        };
        screen.getGui().addWidget(WirelessPage.userPage((TileEntity) te).window);

        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
}