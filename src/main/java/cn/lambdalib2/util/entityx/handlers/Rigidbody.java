package cn.lambdalib2.util.entityx.handlers;

import cn.lambdalib2.util.BlockSelectors;
import cn.lambdalib2.util.IBlockSelector;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import cn.lambdalib2.util.entityx.MotionHandler;
import cn.lambdalib2.util.entityx.event.CollideEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

/**
 * Rigidbody will update velocity and apply gravity and do simple collision.
 * @author WeAthFolD
 */
public class Rigidbody extends MotionHandler
{
    
    public double gravity = 0.00; //block/tick^2
    public double linearDrag = 1.0;
    
    public Predicate<Entity> entitySel;
    public IBlockSelector blockFil = BlockSelectors.filNormal;
    
    public boolean accurateCollision = false;

    @Override
    public String getID() {
        return "Rigidbody";
    }

    @Override
    public void onStart() {}

    @Override
    public void onUpdate() {
        Entity target = getTarget();
        
        //Collision detection
        RayTraceResult result = null;
        if(accurateCollision) {
            float hw = target.width / 2, hh = target.height;
            Vec3d[] points = {
                    new Vec3d(target.posX - hw, target.posY,      target.posZ - hw),
                    new Vec3d(target.posX - hw, target.posY,      target.posZ + hw),
                    new Vec3d(target.posX + hw, target.posY,      target.posZ + hw),
                    new Vec3d(target.posX + hw, target.posY,      target.posZ - hw),
                    new Vec3d(target.posX - hw, target.posY + hh, target.posZ - hw),
                    new Vec3d(target.posX - hw, target.posY + hh, target.posZ + hw),
                    new Vec3d(target.posX + hw, target.posY + hh, target.posZ + hw),
                    new Vec3d(target.posX + hw, target.posY + hh, target.posZ - hw),
            };
            Vec3d motion = new Vec3d(target.motionX, target.motionY, target.motionZ);
            for(Vec3d vec : points) {
                Vec3d next = VecUtils.add(vec, motion);
                World world = target.getEntityWorld();
                if((result = Raytrace.perform(target.getEntityWorld(), vec, next, entitySel, blockFil)) != null)
                    break;
            }
        } else {
            Vec3d cur = new Vec3d(target.posX, target.posY, target.posZ),
                    next = new Vec3d(target.posX + target.motionX, target.posY + target.motionY, target.posZ + target.motionZ);
            result = Raytrace.perform(target.getEntityWorld(), cur, next, entitySel, blockFil);
        }
        
        if(result != null && result.typeOfHit != Type.MISS) {
            getEntityX().postEvent(new CollideEvent(result)); //Let the event handlers do the actual job.
        }
        
        //Velocity update
        target.motionY -= gravity;
        
        target.motionX *= linearDrag;
        target.motionY *= linearDrag;
        target.motionZ *= linearDrag;
        
        target.lastTickPosX = target.posX;
        target.lastTickPosY = target.posY;
        target.lastTickPosZ = target.posZ;
        target.setPosition(target.posX + target.motionX, target.posY + target.motionY, target.posZ + target.motionZ);
    }

}
