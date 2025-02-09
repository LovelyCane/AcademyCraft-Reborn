package cn.lambdalib2.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Some commonly used vector calculation & generations.
 *
 * @author WeAthFolD
 */
public class VecUtils {
    private static final Random rand = new Random();


    public static Vec3d random() {
        return _vec(-1 + 2 * rand.nextDouble(), -1 + 2 * rand.nextDouble(), -1 + 2 * rand.nextDouble());
    }

    public static Vec3d toDirVector(Entity ent, float partialTicks) {
        return toDirVector(MathUtils.lerpDegree(ent.prevRotationYaw, ent.rotationYaw, partialTicks), MathUtils.lerpDegree(ent.prevRotationPitch, ent.rotationPitch, partialTicks));
    }

    /**
     * Convert the yaw and pitch angle to the looking direction vector
     *
     * @param yaw   in mc entity angle space
     * @param pitch in mc entity angle space
     * @return the looking direction vec, normalized
     */
    public static Vec3d toDirVector(float yaw, float pitch) {
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(f2 * f3, f4, f1 * f3);
    }

    public static Vec3d multiply(Vec3d v, double scale) {
        return new Vec3d(v.x * scale, v.y * scale, v.z * scale);
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, double lambda) {
        return new Vec3d(MathUtils.lerp(a.x, b.x, lambda), MathUtils.lerp(a.y, b.y, lambda), MathUtils.lerp(a.z, b.z, lambda));
    }

    public static Vec3d neg(Vec3d v) {
        return new Vec3d(-v.x, -v.y, -v.z);
    }

    public static Vec3d add(Vec3d a, Vec3d b) {
        return new Vec3d(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vec3d subtract(Vec3d a, Vec3d b) {
        return add(a, neg(b));
    }

    public static Vec3d copy(Vec3d v) {
        return new Vec3d(v.x, v.y, v.z);
    }

    public static Vec3d crossProduct(Vec3d a, Vec3d b) {
        double x0 = a.x, y0 = a.y, z0 = a.z, x1 = b.x, y1 = b.y, z1 = b.z;
        return new Vec3d(y0 * z1 - y1 * z0, x1 * z0 - x0 * z1, x0 * y1 - x1 * y0);
    }

    // CREDITS TO Greg S for the original code.
    private static Vec3d getIntersection(double fDst1, double fDst2, Vec3d P1, Vec3d P2) {
        if ((fDst1 * fDst2) >= 0.0f) return null;
        if (fDst1 == fDst2) return null;
        return add(P1, multiply(subtract(P2, P1), (-fDst1 / (fDst2 - fDst1))));
    }

    private static boolean inBox(Vec3d Hit, Vec3d B1, Vec3d B2, int Axis) {
        if (Axis == 1 && Hit.z > B1.z && Hit.z < B2.z && Hit.y > B1.y && Hit.y < B2.y) return true;
        if (Axis == 2 && Hit.z > B1.z && Hit.z < B2.z && Hit.x > B1.x && Hit.x < B2.x) return true;
        return Axis == 3 && Hit.x > B1.x && Hit.x < B2.x && Hit.y > B1.y && Hit.y < B2.y;
    }

    /**
     * Check if the line segment (L1, L2) intersects with AABB represented by (B1, B2).
     * If intersected, return the a hit point of the segment to the line.
     * Else, return null.
     *
     * @param B1 smallest point for AABB
     * @param B2 largest point for AABB
     * @param L1 start point of the line
     * @param L2 end point of the line
     */
    public static Vec3d checkLineBox(Vec3d B1, Vec3d B2, Vec3d L1, Vec3d L2) {
        if (L2.x < B1.x && L1.x < B1.x) return null;
        if (L2.x > B2.x && L1.x > B2.x) return null;
        if (L2.y < B1.y && L1.y < B1.y) return null;
        if (L2.y > B2.y && L1.y > B2.y) return null;
        if (L2.z < B1.z && L1.z < B1.z) return null;
        if (L2.z > B2.z && L1.z > B2.z) return null;

        if (L1.x > B1.x && L1.x < B2.x && L1.y > B1.y && L1.y < B2.y && L1.z > B1.z && L1.z < B2.z) return L1;

        Vec3d Hit;
        if (((Hit = getIntersection(L1.x - B1.x, L2.x - B1.x, L1, L2)) != null && inBox(Hit, B1, B2, 1)) || ((Hit = getIntersection(L1.y - B1.y, L2.y - B1.y, L1, L2)) != null && inBox(Hit, B1, B2, 2)) || ((Hit = getIntersection(L1.z - B1.z, L2.z - B1.z, L1, L2)) != null && inBox(Hit, B1, B2, 3)) || ((Hit = getIntersection(L1.x - B2.x, L2.x - B2.x, L1, L2)) != null && inBox(Hit, B1, B2, 1)) || ((Hit = getIntersection(L1.y - B2.y, L2.y - B2.y, L1, L2)) != null && inBox(Hit, B1, B2, 2)) || ((Hit = getIntersection(L1.z - B2.z, L2.z - B2.z, L1, L2)) != null && inBox(Hit, B1, B2, 3)))
            return Hit;

        return null;
    }

    public static Vec3d entityPos(Entity e) {
        return e.getPositionVector();
    }

    public static Vec3d entityHeadPos(Entity e) {
        return e.getPositionEyes(1f);
    }

    private static Vec3d _vec(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    public static Vec3d lookingPos(Entity e, double range) {
        return add(e.getPositionEyes(1F), multiply(e.getLookVec(), range));
    }

    public static Vec3d rotateAroundZ(Vec3d v, float p_72446_1_) {
        float f1 = MathHelper.cos(p_72446_1_);
        float f2 = MathHelper.sin(p_72446_1_);
        double d0 = v.x * (double) f1 + v.y * (double) f2;
        double d1 = v.y * (double) f1 - v.x * (double) f2;
        double d2 = v.z;
        return new Vec3d(d0, d1, d2);
    }

    public static void setMotion(Entity e, Vec3d motion) {
        e.motionX = motion.x;
        e.motionY = motion.y;
        e.motionZ = motion.z;
    }
}
