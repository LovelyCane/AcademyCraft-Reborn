package cn.academy.internal.support.ic2;

import cn.academy.internal.support.EnergyBlockHelper.IEnergyBlockManager;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author WeAthFolD
 */
public class EUSinkManager implements IEnergyBlockManager {

    @Override
    public boolean isSupported(TileEntity tile) {
        return asSink(tile) != null;
    }

    IEnergySink asSink(TileEntity tile) {
        return tile instanceof IEnergySink ? (IEnergySink) tile : null;
    }

    @Override
    public double getEnergy(TileEntity tile) {
        // NOT SUPPORTED
        return 0;
    }

    @Override
    public void setEnergy(TileEntity tile, double energy) {
        // NOT SUPPORTED
        return;
    }

    @Override
    public double charge(TileEntity tile, double amt, boolean ignoreBandwidth) {
        IEnergySink sink = asSink(tile);
        if(sink != null) {
            return IC2SupportImpl.CONV_RATE * sink.injectEnergy(EnumFacing.UP, amt / IC2SupportImpl.CONV_RATE, ignoreBandwidth ? 233333 : 1024);
        }
        return amt;
    }

    @Override
    public double pull(TileEntity tile, double amt, boolean ignoreBandwidth) {
        // NOT SUPPORTED
        return 0;
    }

}