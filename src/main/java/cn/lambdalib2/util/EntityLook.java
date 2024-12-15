package cn.lambdalib2.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityLook {

    public float yaw, pitch;

    public EntityLook() {}

    public EntityLook(Entity e) {
        this(e.rotationYaw, e.rotationPitch);
    }

    public EntityLook(Vec3d v) {
        yaw = -MathUtils.toDegrees((float) Math.atan2(v.x, v.z));
        pitch = -MathUtils.toDegrees((float) Math.atan2(v.y, Math.sqrt(v.x * v.x + v.z * v.z)));
    }

    public EntityLook(EnumFacing facing) {
        switch (facing) {
            case DOWN:
                yaw = 0; pitch = 90;
                break;
            case UP:
                yaw = 0; pitch = -90;
                break;
            case EAST:
                yaw = 90; pitch = 0;
                break;
            case WEST:
                yaw = -90; pitch = 0;
                break;
            case SOUTH:
                yaw = 0; pitch = 0;
                break;
            case NORTH:
                yaw = 180; pitch = 0;
                break;
        }
    }

    public EntityLook(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vec3d toVec3() {
        float yawRad = MathUtils.toRadians(yaw);
        float pitchRad = MathUtils.toRadians(pitch);
        return new Vec3d(
            -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad),
            -MathHelper.sin(pitchRad),
            MathHelper.cos(yawRad) * MathHelper.cos(pitchRad)
        );
    }

    public void applyToEntity(Entity e) {
        e.rotationYaw = yaw;
        e.rotationPitch = pitch;
    }
}
