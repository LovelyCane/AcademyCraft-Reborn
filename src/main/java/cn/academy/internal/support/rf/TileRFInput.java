package cn.academy.internal.support.rf;

import cn.academy.internal.tileentity.TileGeneratorBase;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

import static cn.academy.internal.support.rf.RFSupportImpl.if2rf;
import static cn.academy.internal.support.rf.RFSupportImpl.rf2if;


@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyReceiver")
public class TileRFInput extends TileGeneratorBase implements IEnergyReceiver {
    public TileRFInput() {
        super("ac_rf_input", 0, 2000, 100);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int amount = (int) rf2if(maxReceive);
        return maxReceive - if2rf(addEnergy(amount, simulate));
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return if2rf(getEnergy());
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return if2rf(2000);
    }

    @Override
    public double getGeneration(double required) {
        return 0;
    }

}