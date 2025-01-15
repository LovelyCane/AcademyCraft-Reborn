package cn.academy.internal.ability.vanilla.electromaster.skill;

public abstract class Target {
    protected double x, y, z;

    public abstract void tick();

    public abstract boolean alive();
}