package cn.academy.ability.vanilla.electromaster.skill;

public abstract class Target {
    protected double x, y, z;

    public abstract void tick();

    public abstract boolean alive();
}