package cn.academy.internal.energy.api;

import cn.academy.internal.energy.api.block.IWirelessReceiver;
import cn.academy.internal.support.EnergyBlockHelper.IEnergyBlockManager;
import net.minecraft.tileentity.TileEntity;

/**
 * @author WeAthFolD
 */

public class IFReceiverManager implements IEnergyBlockManager {
    public static IFReceiverManager instance = new IFReceiverManager();

    private IFReceiverManager() {
    }

    @Override
    public boolean isSupported(TileEntity tile) {
        return tile instanceof IWirelessReceiver;
    }

    @Override
    public double getEnergy(TileEntity tile) {
        return 0;
    }

    @Override
    public void setEnergy(TileEntity tile, double energy) {
        // throw new UnsupportedOperationException();
    }

    @Override
    public double charge(TileEntity tile, double amt, boolean ignoreBandwidth) {
        return ((IWirelessReceiver) tile).injectEnergy(amt);
    }

    @Override
    public double pull(TileEntity tile, double amt, boolean ignoreBandwidth) {
        return ((IWirelessReceiver) tile).pullEnergy(amt);
    }
}