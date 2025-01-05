package cn.academy.ability.vanilla.teleporter.skill;

import cn.academy.AcademyCraft;
import cn.lambdalib2.s11n.SerializeStrategy;
import cn.lambdalib2.s11n.network.NetworkS11nType;

@NetworkS11nType
@SerializeStrategy(strategy = SerializeStrategy.ExposeStrategy.ALL)
public class Location {
    public String name;
    public int dim;
    public float x;
    public float y;
    public float z;
    public int id;

    public Location() {
        AcademyCraft.log.info("Init {}", this);
    }

    public Location(String name, int dim, float[] pos, int id) {
        this.name = name;
        this.dim = dim;
        if (pos != null && pos.length == 3) {
            this.x = pos[0];
            this.y = pos[1];
            this.z = pos[2];
        }
        this.id = id;
        AcademyCraft.log.info("Init {}", this);
    }
}
