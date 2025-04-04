package cn.academy.internal.support.ic2;

import cn.academy.internal.tileentity.TileReceiverBase;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;

import static cn.academy.internal.support.ic2.IC2SupportImpl.eu2if;
import static cn.academy.internal.support.ic2.IC2SupportImpl.if2eu;

/**
 * 
 * @author KSkun
 */
@Optional.Interface(modid = IC2SupportImpl.IC2_MODID, iface = IC2SupportImpl.IC2_IFACE)
public class TileEUOutput extends TileReceiverBase implements IEnergySource {
    
    private boolean isRegistered = false;

    public TileEUOutput() {
        super("ac_eu_output", 0, 2000, 100);
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return if2eu(energy);
    }

    @Override
    public void drawEnergy(double amount) {
        energy -= eu2if(amount);
        if(energy < 0d) energy = 0d;
    }

    @Override
    public int getSourceTier() {
        return 2;
    }

    @Override
    public void onLoad()
    {
        if(!getWorld().isRemote)
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
        super.onLoad();
    }
    
    @Override
    public void onChunkUnload() {
        if(!getWorld().isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        }
        super.onChunkUnload();
    }
    
    @Override
    public void invalidate() {
        if(!getWorld().isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        }
        super.invalidate();
    }

}