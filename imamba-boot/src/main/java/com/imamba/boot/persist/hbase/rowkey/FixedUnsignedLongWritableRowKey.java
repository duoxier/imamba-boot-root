package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class FixedUnsignedLongWritableRowKey extends FixedLongWritableRowKey {
    public FixedUnsignedLongWritableRowKey() {
    }

    protected LongWritable invertSign(LongWritable lw) {
        lw.set(lw.get() ^ -9223372036854775808L);
        return lw;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        this.invertSign((LongWritable)o);
        super.serialize(o, w);
        this.invertSign((LongWritable)o);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        return this.invertSign((LongWritable)super.deserialize(w));
    }
}