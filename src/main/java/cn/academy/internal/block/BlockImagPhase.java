package cn.academy.internal.block;

import cn.academy.AcademyCraft;
import cn.academy.internal.tileentity.TileImagPhase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;

/**
 * TODO Implement particle and fog effect
 * @author WeAthFolD
 */
public class BlockImagPhase extends BlockFluidClassic implements ITileEntityProvider {

    public BlockImagPhase() {
        super(ACFluids.fluidImagProj, Material.WATER);
        setCreativeTab(AcademyCraft.cct);

        this.setQuantaPerBlock(3);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileImagPhase();
    }
}