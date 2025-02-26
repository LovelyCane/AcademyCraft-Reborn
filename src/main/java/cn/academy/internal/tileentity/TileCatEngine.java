package cn.academy.internal.tileentity;

import cn.lambdalib2.util.TickScheduler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Cat Engine!
 *
 * @author WeAthFolD
 */
public class TileCatEngine extends TileGeneratorBase {
    private final TickScheduler scheduler = new TickScheduler();

    // Intrusive render parameters
    public double thisTickGen;
    public double rotation;
    public long lastRender;

    public TileCatEngine() {
        super("infinite_generator", 0, 2000, 200);
    }

    @Override
    public void update() {
        super.update();
        scheduler.runTick();
    }

    @Override
    public double getGeneration(double required) {
        return (thisTickGen = Math.min(required, 500));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setDouble("tickGen", thisTickGen);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        thisTickGen = tag.getDouble("tickGen");
    }
}