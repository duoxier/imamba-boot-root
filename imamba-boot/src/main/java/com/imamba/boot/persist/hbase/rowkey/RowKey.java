package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import java.io.IOException;

public abstract class RowKey {
    protected Order order;
    protected Termination termination;
    private ImmutableBytesWritable w;

    public RowKey() {
        this.termination = Termination.AUTO;
        this.order = Order.ASCENDING;
    }

    public RowKey setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public Termination getTermination() {
        return this.termination;
    }

    public RowKey setTermination(Termination termination) {
        this.termination = termination;
        return this;
    }

    boolean terminate() {
        switch(this.termination) {
            case SHOULD_NOT:
                return false;
            case MUST:
                return true;
            case AUTO:
                return this.order == Order.DESCENDING;
            default:
                throw new IllegalStateException("unknown termination " + this.termination);
        }
    }

    public abstract Class<?> getSerializedClass();

    public Class<?> getDeserializedClass() {
        return this.getSerializedClass();
    }

    public abstract int getSerializedLength(Object var1) throws IOException;

    public abstract void serialize(Object var1, ImmutableBytesWritable var2) throws IOException;

    public void serialize(Object o, byte[] b) throws IOException {
        this.serialize(o, b, 0);
    }

    public void serialize(Object o, byte[] b, int offset) throws IOException {
        if (this.w == null) {
            this.w = new ImmutableBytesWritable();
        }

        this.w.set(b, offset, b.length - offset);
        this.serialize(o, this.w);
    }

    public byte[] serialize(Object o) throws IOException {
        byte[] b = new byte[this.getSerializedLength(o)];
        this.serialize(o, b, 0);
        return b;
    }

    public abstract void skip(ImmutableBytesWritable var1) throws IOException;

    public abstract Object deserialize(ImmutableBytesWritable var1) throws IOException;

    public Object deserialize(byte[] b) throws IOException {
        return this.deserialize(b, 0);
    }

    public Object deserialize(byte[] b, int offset) throws IOException {
        if (this.w == null) {
            this.w = new ImmutableBytesWritable();
        }

        this.w.set(b, offset, b.length - offset);
        return this.deserialize(this.w);
    }

    protected byte mask(byte b) {
        return (byte)(b ^ this.order.mask());
    }
}