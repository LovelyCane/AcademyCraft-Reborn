package cn.academy.internal.tileentity;

import cn.academy.internal.energy.IFConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author WeAthFolD
 */
public class TileSolarGen extends TileGeneratorBase {

    public static final int SLOT_BATTERY = 0;

    public enum SolarStatus {
        STOPPED, WEAK, STRONG
    }

    public TileSolarGen() {
        super("solar_generator", 1, 1000, IFConstants.LATENCY_MK2);
    }

    @Override
    public double getGeneration(double required) {
        World world = this.getWorld();
        double brightLev = canGenerate() ? 1.0 : 0.0;
        brightLev *= world.isRaining() ? 0.2 : 1.0;

        return Math.min(required, brightLev * 3.0);
    }

    public SolarStatus getStatus() {
        World world = getWorld();
        if (canGenerate()) {
            return world.isRaining() ? SolarStatus.WEAK : SolarStatus.STRONG;
        } else {
            return SolarStatus.STOPPED;
        }
    }

    private boolean canGenerate() {
        World world = getWorld();
        long time = world.getWorldTime() % 24000;
        boolean isDay = time >= 0 && time <= 12500;
        return isDay && world.canSeeSky(getPos().add(0, 1, 0));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        return tag;
    }
}