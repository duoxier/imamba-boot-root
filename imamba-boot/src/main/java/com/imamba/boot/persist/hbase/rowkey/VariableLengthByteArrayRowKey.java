package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.BytesWritable;

import java.io.IOException;

public class VariableLengthByteArrayRowKey extends VariableLengthBytesWritableRowKey {
    public VariableLengthByteArrayRowKey() {
    }

    public VariableLengthByteArrayRowKey(int fixedPrefixLength) {
        super(fixedPrefixLength);
    }

    public Class<?> getSerializedClass() {
        return byte[].class;
    }

    protected Object toBytesWritable(Object o) {
        if (o != null && !(o instanceof BytesWritable)) {
            BytesWritable bw = new BytesWritable();
            byte[] bytes = (byte[])((byte[])o);
            bw.set(bytes, 0, bytes.length);
            return bw;
        } else {
            return o;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toBytesWritable(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toBytesWritable(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        BytesWritable bw = (BytesWritable)super.deserialize(w);
        if (bw == null) {
            return null;
        } else {
            byte[] result = new byte[bw.getLength()];
            System.arraycopy(bw.getBytes(), 0, result, 0, bw.getLength());
            return result;
        }
    }
}