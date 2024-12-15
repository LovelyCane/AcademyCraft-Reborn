package cn.lambdalib2.util.entityx.event;

import cn.lambdalib2.util.entityx.EntityEvent;
import cn.lambdalib2.util.entityx.EntityEventHandler;
import net.minecraft.util.math.RayTraceResult;

/**
 * @author WeAthFolD
 */
public class CollideEvent extends EntityEvent
{

    public final RayTraceResult result;
    
    public CollideEvent(RayTraceResult rtr) {
        result = rtr;
    }
    
    public static abstract class CollideHandler extends EntityEventHandler<CollideEvent>
    {

        @Override
        public Class<? extends EntityEvent> getHandledEvent() {
            return CollideEvent.class;
        }
        
    }
    
}
