package com.imamba.boot.persist.hbase.rowkey;

import java.util.ArrayList;
import java.util.List;

public class StructBuilder {
    protected List<RowKey> fields = new ArrayList();
    protected Order order;

    public StructBuilder() {
        this.order = Order.ASCENDING;
    }

    public StructBuilder add(RowKey key) {
        this.fields.add(key);
        return this;
    }

    public StructBuilder set(int i, RowKey key) {
        this.fields.set(i, key);
        return this;
    }

    public RowKey get(int i) {
        return (RowKey)this.fields.get(i);
    }

    public List<RowKey> getFields() {
        return this.fields;
    }

    public StructBuilder setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public StructRowKey toRowKey() {
        RowKey[] fields = (RowKey[])this.fields.toArray(new RowKey[0]);
        return (StructRowKey)(new StructRowKey(fields)).setOrder(this.order);
    }

    public StructBuilder reset() {
        this.fields.clear();
        this.order = Order.ASCENDING;
        return this;
    }
}
