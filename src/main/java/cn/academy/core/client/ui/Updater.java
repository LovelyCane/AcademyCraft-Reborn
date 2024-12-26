package cn.academy.core.client.ui;

public interface Updater<T> {
    void add(T obj);

    void clear();

    void apply(float alpha);

    void apply(T obj, float alpha);
}