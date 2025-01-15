package cn.academy.internal.ability.vanilla.electromaster.skill;

public class PointTarget extends Target {
    public PointTarget(double _x, double _y, double _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean alive() {
        return true;
    }
}