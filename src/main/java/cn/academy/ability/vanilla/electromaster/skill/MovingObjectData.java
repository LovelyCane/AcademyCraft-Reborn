package cn.academy.ability.vanilla.electromaster.skill;

import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.SerializeNullable;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import net.minecraft.util.math.Vec3d;

@NetworkS11nType
public class MovingObjectData {
    @SerializeIncluded
    public boolean isNull = false;

    @SerializeIncluded
    public int blockX = 0;

    @SerializeIncluded
    public int blockY = 0;

    @SerializeIncluded
    public int blockZ = 0;

    @SerializeIncluded
    public boolean isEntity = false;

    @SerializeIncluded
    public double entityEyeHeight = 0d;

    @SerializeIncluded
    @SerializeNullable
    public Vec3d hitVec;
}
