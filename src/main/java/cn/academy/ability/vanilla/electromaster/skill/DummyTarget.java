package cn.academy.ability.vanilla.electromaster.skill;

public class DummyTarget extends Target {
    public DummyTarget(double _x, double _y, double _z) {
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
