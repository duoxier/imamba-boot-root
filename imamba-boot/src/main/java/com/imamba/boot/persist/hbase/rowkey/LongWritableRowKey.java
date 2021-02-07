package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

public class LongWritableRowKey extends AbstractVarIntRowKey {
    protected static final byte LONG_SIGN = -128;
    protected static final byte LONG_SINGLE = 64;
    protected static final byte LONG_DOUBLE = 32;
    protected static final int LONG_SINGLE_DATA_BITS = 6;
    protected static final int LONG_DOUBLE_DATA_BITS = 5;
    protected static final int LONG_EXT_DATA_BITS = 2;
    protected static final int LONG_EXT_LENGTH_BITS = 3;

    public LongWritableRowKey() {
        super((byte)64, 6, (byte)32, 5, 3, 2);
    }

    public Class<?> getSerializedClass() {
        return LongWritable.class;
    }

    Writable createWritable() {
        return new LongWritable();
    }

    void setWritable(long x, Writable w) {
        ((LongWritable)w).set(x);
    }

    long getWritable(Writable w) {
        return ((LongWritable)w).get();
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