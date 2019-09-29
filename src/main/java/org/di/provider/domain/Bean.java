package org.di.provider.domain;

public class Bean<T> {
    private T object;
    private Class<T> clazz;

    public Bean(T object, Class<T> clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    public T getObject() {
        return object;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
