package cn.academy.internal.ability.vanilla.electromaster.skill;

import net.minecraft.entity.Entity;

public class EntityTarget extends Target {
    private final Entity target;

    public EntityTarget(Entity _t) {
        this.target = _t;
    }

    @Override
    public void tick() {
        this.x = target.posX;
        this.y = target.posY + target.getEyeHeight();
        this.z = target.posZ;
    }

    @Override
    public boolean alive() {
        return !target.isDead;
    }
}