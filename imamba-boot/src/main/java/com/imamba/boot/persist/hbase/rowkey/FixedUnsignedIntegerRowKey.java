package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;

public class FixedUnsignedIntegerRowKey extends FixedUnsignedIntWritableRowKey {
    private IntWritable iw;

    public FixedUnsignedIntegerRowKey() {
    }

    public Class<?> getSerializedClass() {
        return Integer.class;
    }

    protected Object toIntWritable(Object o) {
        if (o != null && !(o instanceof IntWritable)) {
            if (this.iw == null) {
                this.iw = new IntWritable();
            }

            this.iw.set((Integer)o);
            return this.iw;
        } else {
            return o;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toIntWritable(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toIntWritable(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        IntWritable iw = (IntWritable)super.deserialize(w);
        return iw == null ? iw : iw.get();
    }
}
