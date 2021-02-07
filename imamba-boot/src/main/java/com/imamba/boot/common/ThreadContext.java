package com.imamba.boot.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ThreadContext {

    private static final Logger log = LoggerFactory.getLogger(ThreadContext.class);
    public static final String SUBJECT_KEY = ThreadContext.class.getName() + "_SUBJECT_KEY";
    private static final ThreadLocal<Map<Object, Object>> resources = new ThreadContext.InheritableThreadLocalMap();

    protected ThreadContext() {
    }

    public static Map<Object, Object> getResources() {
        return resources != null ? new HashMap((Map)resources.get()) : null;
    }

    public static void setResources(Map<Object, Object> newResources) {
        if (!isEmpty(newResources)) {
            ((Map)resources.get()).clear();
            ((Map)resources.get()).putAll(newResources);
        }
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    private static Object getValue(Object key) {
        return ((Map)resources.get()).get(key);
    }

    public static <T> T get(Object key) {
        if (log.isTraceEnabled()) {
            String msg = "get() - in thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }

        Object value = getValue(key);
        if (value != null && log.isTraceEnabled()) {
            String msg = "Retrieved value of type [" + value.getClass().getName() + "] for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }

        return (T) value;
    }

    public static void put(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        } else if (value == null) {
            remove(key);
        } else {
            ((Map)resources.get()).put(key, value);
            if (log.isTraceEnabled()) {
                String msg = "Bound value of type [" + value.getClass().getName() + "] for key [" + key + "] to thread [" + Thread.currentThread().getName() + "]";
                log.trace(msg);
            }

        }
    }

    public static Object remove(Object key) {
        Object value = ((Map)resources.get()).remove(key);
        if (value != null && log.isTraceEnabled()) {
            String msg = "Removed value of type [" + value.getClass().getName() + "] for key [" + key + "]from thread [" + Thread.currentThread().getName() + "]";
            log.trace(msg);
        }

        return value;
    }

    public static void remove() {
        resources.remove();
    }

    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {
        private InheritableThreadLocalMap() {
        }

        protected Map<Object, Object> initialValue() {
            return new HashMap();
        }

        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            return parentValue != null ? (Map)((HashMap)parentValue).clone() : null;
        }
    }
}
