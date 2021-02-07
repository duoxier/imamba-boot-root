package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

public class UnsignedLongWritableRowKey extends AbstractVarIntRowKey {
    protected static final byte ULONG_SINGLE = -128;
    protected static final byte ULONG_DOUBLE = 64;
    protected static final int ULONG_SINGLE_DATA_BITS = 7;
    protected static final int ULONG_DOUBLE_DATA_BITS = 6;
    protected static final int ULONG_EXT_DATA_BITS = 3;
    protected static final int ULONG_EXT_LENGTH_BITS = 3;

    public UnsignedLongWritableRowKey() {
        super((byte)-128, 7, (byte)64, 6, 3, 3);
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
        return 0L;
    }

    protected byte initHeader(boolean sign) {
        return 0;
    }

    protected byte getSign(byte h) {
        return 0;
    }

    protected byte serializeNonNullHeader(byte b) {
        return (byte)(b + 1);
    }

    protected byte deserializeNonNullHeader(byte b) {
        return (byte)(b - 1);
    }
}
