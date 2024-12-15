package cn.lambdalib2.util;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import static cn.lambdalib2.util.VecUtils.*;

/**
 * A better wrap up for ray trace routines, supporting entity filtering, block filtering, and combined RayTrace of
 * blocks and entities. Also provided functions for fast implementation on entity looking traces.
 * @author WeAthFolD
 */
public class Raytrace {

    private static RayTraceResult miss(Vec3d pos) {
        RayTraceResult ret = new RayTraceResult(Type.MISS, pos, EnumFacing.UP,
            new BlockPos((int) pos.x, (int) pos.y, (int) pos.z));
        return ret;
    }

    /**
     * Perform a ray trace.
     * @param world The world to perform on
     * @param vec1 Start point
     * @param vec2 End point
     * @param entityPred The entity predicate
     * @param blockSel The block predicate
     * @return The trace result, might be null
     */
    public static RayTraceResult perform(World world, Vec3d vec1, Vec3d vec2, Predicate<Entity> entityPred, IBlockSelector blockSel) {
        RayTraceResult
            mop1 = rayTraceEntities(world, vec1, vec2, entityPred),
            mop2 = rayTraceBlocks(world, vec1, vec2, blockSel);

        if(mop1 != null && mop2 != null) {
            double d1 = mop1.hitVec.distanceTo(vec1);
            double d2 = mop2.hitVec.distanceTo(vec1);
            return d1 <= d2 ? mop1 : mop2;
        }
        if(mop1 != null)
            return mop1;
    
        return mop2 == null ? miss(vec2) : mop2;
    }
    
    public static RayTraceResult perform(World world, Vec3d vec1, Vec3d vec2, Predicate<Entity> entityPred) {
        return perform(world, vec1, vec2, entityPred, null);
    }
    
    public static RayTraceResult perform(World world, Vec3d vec1, Vec3d vec2) {
        return perform(world, vec1, vec2, null, null);
    }
    
    public static Pair<Vec3d, RayTraceResult> getLookingPos(EntityLivingBase living, double dist) {
        return getLookingPos(living, dist, null, null);
    }
    
    public static Pair<Vec3d, RayTraceResult> getLookingPos(EntityLivingBase living, double dist,
                                                                 Predicate<Entity> pred) {
        return getLookingPos(living, dist, pred, null);
    }
    
    public static Pair<Vec3d, RayTraceResult> getLookingPos(Entity living, double dist,
                                                                 Predicate<Entity> esel, IBlockSelector bsel) {
        RayTraceResult pos = traceLiving(living, dist, esel, bsel);
        Vec3d end = null;
        if(pos != null) {
            end = pos.hitVec;
            if(pos.entityHit != null)
                end = new Vec3d(end.x, end.y + pos.entityHit.getEyeHeight() * 0.6, end.z);
        }
        if(end == null) {
            Vec3d lookingPos = living.getLookVec();
            end = VecUtils.add(living.getPositionVector(),VecUtils.multiply(lookingPos, dist));
        }
        
        return Pair.of(end, pos);
    }
    
    public static RayTraceResult rayTraceEntities(World world, Vec3d vec1, Vec3d vec2, Predicate<? super Entity> selector) {
        Entity entity = null;
        AxisAlignedBB boundingBox = WorldUtils.getBoundingBox(vec1, vec2);
        List list = world.getEntitiesInAABBexcluding(null, boundingBox.expand(1.0D, 1.0D, 1.0D),
                selector==null?null:selector::test);
        //TODO Make sure which predicate to be used.
        double d0 = 0.0D;

        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = (Entity)list.get(j);

            if(!entity1.canBeCollidedWith() || (selector != null && !selector.test(entity1)))
                continue;
            
            float f = 0.3F;
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f, f, f);
            RayTraceResult mob1 = axisalignedbb.calculateIntercept(vec1, vec2);

            if (mob1 != null) {
                double d1 = vec1.distanceTo(mob1.hitVec);

                if (d1 < d0 || d0 == 0.0D)
                {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        if (entity != null) {
            RayTraceResult result = new RayTraceResult(entity);

            //note: fix, get real entityId for ender dragon, maybe some other mod have different structures
//            if (entity instanceof MultiPartEntityPart) {
//                if (((MultiPartEntityPart) entity).parent instanceof Entity) {
//                    result.entityHit = (Entity)((MultiPartEntityPart) entity).parent;
//                }
//            }
            return result;
        }
        return null;
    }
    
    /**
     * Mojang code with minor changes to support block filtering.
     * @param world world
     * @param start startPoint
     * @param end endPoint
     * @param filter BlockFilter
     * @return RayTraceResult
     */
    public static RayTraceResult rayTraceBlocks(World world, Vec3d start, Vec3d end, IBlockSelector filter) {
        if(filter == null)
            filter = BlockSelectors.filNormal;

        Vec3d current = VecUtils.copy(start);
        
        final int x2 = MathHelper.floor(end.x);
        final int y2 = MathHelper.floor(end.y);
        final int z2 = MathHelper.floor(end.z);

        int x1 = MathHelper.floor(current.x);
        int y1 = MathHelper.floor(current.y);
        int z1 = MathHelper.floor(current.z);
        {
            BlockPos pos = new BlockPos(x1, y1, z1);
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (filter.accepts(world, x1, y1, z1, block)) {
                RayTraceResult result = state.collisionRayTrace(world, pos, current, end);
                if (result != null) {
                    return result;
                }
            }
        }

        for (int i = 0; i < 200; ++i) {
            if (x1 == x2 && y1 == y2 && z1 == z2) {
                return null;
            }

            boolean moveX = true;
            boolean moveY = true;
            boolean moveZ = true;
            double nextX = 999.0D;
            double nextY = 999.0D;
            double nextZ = 999.0D;

            if (x2 > x1) {
                nextX = x1 + 1.0D;
            } else if (x2 < x1) {
                nextX = x1 + 0.0D;
            } else {
                moveX = false;
            }

            if (y2 > y1) {
                nextY = y1 + 1.0D;
            } else if (y2 < y1) {
                nextY = y1 + 0.0D;
            } else {
                moveY = false;
            }

            if (z2 > z1) {
                nextZ = z1 + 1.0D;
            } else if (z2 < z1) {
                nextZ = z1 + 0.0D;
            } else {
                moveZ = false;
            }

            double xFactor = 999.0D;
            double yFactor = 999.0D;
            double zFactor = 999.0D;
            double dx = end.x - current.x;
            double dy = end.y - current.y;
            double dz = end.z - current.z;

            if (moveX) {
                xFactor = (nextX - current.x) / dx;
            }
            if (moveY) {
                yFactor = (nextY - current.y) / dy;
            }
            if (moveZ) {
                zFactor = (nextZ - current.z) / dz;
            }
            byte side;

            if (xFactor < yFactor && xFactor < zFactor) {
                if (x2 > x1) {
                    side = 4;
                } else {
                    side = 5;
                }
                current = new Vec3d(nextX, current.y + dy * xFactor, current.z + dz*xFactor);
            } else if (yFactor < zFactor) {
                if (y2 > y1) {
                    side = 0;
                } else {
                    side = 1;
                }
                current = new Vec3d(current.x + dx * yFactor, nextY, current.z + dz * yFactor);
            } else {
                if (z2 > z1) {
                    side = 2;
                } else {
                    side = 3;
                }
                current = new Vec3d(current.x + dx * zFactor, current.y + dy * zFactor, nextZ);
            }

            x1 = MathHelper.floor(current.x);
            if (side == 5) {
                --x1;
            }

            y1 = MathHelper.floor(current.y);
            if (side == 1) {
                --y1;
            }

            z1 = MathHelper.floor(current.z);
            if (side == 3) {
                --z1;
            }
            BlockPos pos = new BlockPos(x1, y1, z1);
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (filter.accepts(world, x1, y1, z1, block)) {
                RayTraceResult result = state.collisionRayTrace(world, pos, current, end);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    public static RayTraceResult traceLiving(Entity entity, double dist) {
        return traceLiving(entity, dist, null, null);
    }
    
    public static RayTraceResult traceLiving(Entity entity, double dist, Predicate<Entity> pred) {
        return traceLiving(entity, dist, pred, null);
    }
    
    /**
     * Performs a RayTrace starting from the target entity's eye towards its looking direction.
     * The trace will automatically ignore the target entity.
     */
    public static RayTraceResult traceLiving(Entity entity, double dist, Predicate<Entity> pred, IBlockSelector blockSel) {
        Vec3d v1 = entity.getPositionEyes(1),
                v2 = add(v1, multiply(entity.getLookVec(), dist));
        
        Predicate<Entity> exclude = EntitySelectors.exclude(entity);
        
        return perform(entity.getEntityWorld(), v1, v2, pred == null ? exclude : exclude.and(pred), blockSel);
    }
    

}
