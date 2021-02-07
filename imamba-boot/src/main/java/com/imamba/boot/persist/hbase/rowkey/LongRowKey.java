package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class LongRowKey extends LongWritableRowKey {
    private LongWritable lw;

    public LongRowKey() {
    }

    public Class<?> getSerializedClass() {
        return Long.class;
    }

    protected Object toLongWritable(Object o) {
        if (o != null && !(o instanceof LongWritable)) {
            if (this.lw == null) {
                this.lw = new LongWritable();
            }

            this.lw.set((Long)o);
            return this.lw;
        } else {
            return o;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toLongWritable(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toLongWritable(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        LongWritable lw = (LongWritable)super.deserialize(w);
        return lw == null ? lw : lw.get();
    }
}