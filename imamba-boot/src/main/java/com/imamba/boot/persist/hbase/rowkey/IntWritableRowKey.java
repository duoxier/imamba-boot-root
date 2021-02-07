package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class IntWritableRowKey extends AbstractVarIntRowKey {
    protected static final byte INT_SIGN = -128;
    protected static final byte INT_SINGLE = 64;
    protected static final byte INT_DOUBLE = 32;
    protected static final int INT_SINGLE_DATA_BITS = 6;
    protected static final int INT_DOUBLE_DATA_BITS = 5;
    protected static final int INT_EXT_DATA_BITS = 3;
    protected static final int INT_EXT_LENGTH_BITS = 2;

    public IntWritableRowKey() {
        super((byte)64, 6, (byte)32, 5, 2, 3);
    }

    public Class<?> getSerializedClass() {
        return IntWritable.class;
    }

    Writable createWritable() {
        return new IntWritable();
    }

    void setWritable(long x, Writable w) {
        ((IntWritable)w).set((int)x);
    }

    long getWritable(Writable w) {
        return (long)((IntWritable)w).get();
    }

    long getSign(long l) {
        return l & -9223372036854775808L;
    }

    protected byte initHeader(boolean sign) {
        return (byte)(sign ? 0 : -128);
    }

    protected byte getSign(byte h) {
        return (byte)((h & -128) != 0 ? 0 : -128);
    }
}
