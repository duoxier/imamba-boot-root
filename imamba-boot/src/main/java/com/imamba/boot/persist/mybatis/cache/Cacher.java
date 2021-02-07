package com.imamba.boot.persist.mybatis.cache;

public class Cacher {
    private Object value;

    public Cacher() {
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}