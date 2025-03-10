package cn.academy.internal.support.rf;

import cn.academy.internal.support.EnergyBlockHelper.IEnergyBlockManager;
import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
/**
 * @author WeAthFolD
 */
public class RFProviderManager implements IEnergyBlockManager {
    
    static final EnumFacing dir = EnumFacing.UP;

    @Override
    public boolean isSupported(TileEntity tile) {
        return asProvider(tile) != null;
    }
    
    private IEnergyProvider asProvider(TileEntity te) {
        return te instanceof IEnergyProvider ? (IEnergyProvider) te : null;
    }

    @Override
    public double getEnergy(TileEntity tile) {
        IEnergyProvider provider = asProvider(tile);
        return provider.getEnergyStored(dir) * RFSupportImpl.CONV_RATE;
    }

    @Override
    public void setEnergy(TileEntity tile, double energy) {
        // NOT SUPPORTED
    }

    @Override
    public double charge(TileEntity tile, double amt, boolean ignoreBandwidth) {
        // NOT SUPPORTED
        return amt;
    }

    @Override
    public double pull(TileEntity tile, double amt, boolean ignoreBandwidth) {
        IEnergyProvider provider = asProvider(tile);
        return provider == null ? 0 : RFSupportImpl.CONV_RATE * provider.extractEnergy(dir, (int) (amt / RFSupportImpl.CONV_RATE), false);
    }

}