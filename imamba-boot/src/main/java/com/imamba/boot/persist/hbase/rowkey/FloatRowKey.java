package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.FloatWritable;

import java.io.IOException;

public class FloatRowKey extends FloatWritableRowKey {
    private FloatWritable fw;

    public FloatRowKey() {
    }

    public Class<?> getSerializedClass() {
        return Float.class;
    }

    protected Object toFloatWritable(Object o) {
        if (o != null && !(o instanceof FloatWritable)) {
            if (this.fw == null) {
                this.fw = new FloatWritable();
            }

            this.fw.set((Float)o);
            return this.fw;
        } else {
            return o;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toFloatWritable(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toFloatWritable(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        FloatWritable fw = (FloatWritable)super.deserialize(w);
        return fw == null ? fw : fw.get();
    }
}