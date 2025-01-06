package cn.academy.ability.vanilla.electromaster.skill;

import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.SerializeNullable;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@NetworkS11nType
public class AttackData {
    @SerializeIncluded
    public List<Entity> aoes;
    @SerializeIncluded
    @SerializeNullable
    public Entity target;
    @SerializeIncluded
    @SerializeNullable
    public Vec3d point;
}
