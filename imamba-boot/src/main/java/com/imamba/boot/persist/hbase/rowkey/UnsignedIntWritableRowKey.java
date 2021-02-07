package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class UnsignedIntWritableRowKey extends AbstractVarIntRowKey {
    protected static final byte ULONG_SINGLE = -128;
    protected static final byte ULONG_DOUBLE = 64;
    protected static final int ULONG_SINGLE_DATA_BITS = 7;
    protected static final int ULONG_DOUBLE_DATA_BITS = 6;
    protected static final int ULONG_EXT_DATA_BITS = 4;
    protected static final int ULONG_EXT_LENGTH_BITS = 2;

    public UnsignedIntWritableRowKey() {
        super((byte)-128, 7, (byte)64, 6, 2, 4);
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
        int i = ((IntWritable)w).get();
        return (long)i & 4294967295L;
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