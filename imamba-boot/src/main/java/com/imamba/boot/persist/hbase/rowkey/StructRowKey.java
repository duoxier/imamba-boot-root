package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import java.io.IOException;

public class StructRowKey extends RowKey implements Iterable<Object> {
    private RowKey[] fields;
    private Object[] v;
    private StructIterator iterator;
    private ImmutableBytesWritable iw;

    public StructRowKey(RowKey[] fields) {
        this.setFields(fields);
    }

    public RowKey setOrder(Order order) {
        if (order == this.getOrder()) {
            return this;
        } else {
            super.setOrder(order);
            RowKey[] var2 = this.fields;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                RowKey field = var2[var4];
                field.setOrder(field.getOrder() == Order.ASCENDING ? Order.DESCENDING : Order.ASCENDING);
            }

            return this;
        }
    }

    public StructRowKey setFields(RowKey[] fields) {
        this.fields = fields;
        return this;
    }

    public RowKey[] getFields() {
        return this.fields;
    }

    public Class<?> getSerializedClass() {
        return Object[].class;
    }

    private Object[] toValues(Object obj) {
        Object[] o = (Object[])((Object[])obj);
        if (o.length != this.fields.length) {
            throw new IndexOutOfBoundsException("Expected " + this.fields.length + " values but got " + o.length + " values");
        } else {
            return o;
        }
    }

    private int setTerminateAndGetLength(Object[] o) throws IOException {
        int len = 0;
        Termination fieldTerm = this.termination;

        for(int i = o.length - 1; i >= 0; --i) {
            if (fieldTerm == Termination.SHOULD_NOT || this.fields[i].getTermination() != Termination.SHOULD_NOT) {
                this.fields[i].setTermination(fieldTerm);
            }

            int objLen = this.fields[i].getSerializedLength(o[i]);
            if (objLen > 0) {
                fieldTerm = Termination.MUST;
                len += objLen;
            }
        }

        return len;
    }

    public int getSerializedLength(Object obj) throws IOException {
        return this.setTerminateAndGetLength(this.toValues(obj));
    }

    public void serialize(Object obj, ImmutableBytesWritable w) throws IOException {
        Object[] o = this.toValues(obj);
        this.setTerminateAndGetLength(o);

        for(int i = 0; i < o.length; ++i) {
            this.fields[i].serialize(o[i], w);
        }

    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        for(int i = 0; i < this.fields.length; ++i) {
            this.fields[i].skip(w);
        }

    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        if (this.v == null) {
            this.v = new Object[this.fields.length];
        }

        for(int i = 0; i < this.fields.length; ++i) {
            this.v[i] = this.fields[i].deserialize(w);
        }

        return this.v;
    }

    public StructRowKey iterateOver(ImmutableBytesWritable iw) {
        this.iw = iw;
        return this;
    }

    public StructRowKey iterateOver(byte[] b, int offset) {
        if (this.iw == null) {
            this.iw = new ImmutableBytesWritable();
        }

        this.iw.set(b, offset, b.length - offset);
        return this;
    }

    public StructRowKey iterateOver(byte[] b) {
        return this.iterateOver(b, 0);
    }

    public StructIterator iterator() {
        if (this.iterator == null) {
            this.iterator = new StructIterator(this);
        }

        this.iterator.reset();
        this.iterator.setBytes(this.iw);
        return this.iterator;
    }
}