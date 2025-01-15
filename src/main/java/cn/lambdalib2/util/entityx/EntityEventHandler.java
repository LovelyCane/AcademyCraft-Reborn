package cn.lambdalib2.util.entityx;

/**
 * @author WeAthFolD
 */
public abstract class EntityEventHandler<T extends EntityEvent> {
    
    public boolean active = true;

    public abstract Class<? extends EntityEvent> getHandledEvent();
    
    public abstract void onEvent(T event);
}