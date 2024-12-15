package cn.lambdalib2.util.entityx;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Hardcoded with an EntityX. Use this for fast implementation.
 * @author WeAthFolD
 */
public abstract class EntityAdvanced extends Entity {
    
    public EntityX entityX;

    public EntityAdvanced(World world) {
        super(world);
        entityX = new EntityX(this);
    }

    @Override
    protected void entityInit() {}

    private boolean firstUpdate = true;
    @Override
    public void onUpdate() {
        if(firstUpdate) {
            firstUpdate = false;
            entityX.startUpdate();
            onFirstUpdate();
        }
        entityX.update();
    }
    
    public boolean firstUpdated() {
        return !firstUpdate;
    }
    
    protected void onFirstUpdate() {
        
    }
    
    /**
     * Designed for prototype pattern.
     */
    public void reset() {
        firstUpdate = true;
    }
    
    public void addMotionHandler(MotionHandler mh) {
        entityX.addMotionHandler(mh);
    }
    
    public void removeMotionHandlers() {
        entityX.removeMotionHandlers();
    }
    
    public <U extends MotionHandler> U getMotionHandler(Class<? extends U> klass) {
        return (U) entityX.getMotionHandler(klass);
    }
    
    public void execute(EntityCallback c) {
        entityX.execute(c);
    }
    
    public void executeAfter(EntityCallback c, int ticks) {
        entityX.executeAfter(c, ticks);
    }
    
    public void postEvent(EntityEvent event) {
        entityX.postEvent(event);
    }
    
    public void regEventHandler(EntityEventHandler eeh) {
        entityX.regEventHandler(eeh);
    }

    public void resetEntityX() {
        entityX = new EntityX(this);
    }
    
}
