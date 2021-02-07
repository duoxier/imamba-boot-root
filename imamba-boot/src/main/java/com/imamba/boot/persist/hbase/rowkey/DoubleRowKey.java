package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.DoubleWritable;

import java.io.IOException;

public class DoubleRowKey extends DoubleWritableRowKey {
    private DoubleWritable dw;

    public DoubleRowKey() {
    }

    public Class<?> getSerializedClass() {
        return Double.class;
    }

    protected Object toDoubleWritable(Object o) {
        if (o != null && !(o instanceof DoubleWritable)) {
            if (this.dw == null) {
                this.dw = new DoubleWritable();
            }

            this.dw.set((Double)o);
            return this.dw;
        } else {
            return o;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toDoubleWritable(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toDoubleWritable(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        DoubleWritable dw = (DoubleWritable)super.deserialize(w);
        return dw == null ? dw : dw.get();
    }
}
