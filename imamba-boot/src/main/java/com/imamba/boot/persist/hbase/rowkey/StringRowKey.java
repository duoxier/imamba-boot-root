package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class StringRowKey extends UTF8RowKey {
    public StringRowKey() {
    }

    public Class<?> getSerializedClass() {
        return String.class;
    }

    protected Object toUTF8(Object o) {
        return o != null && !(o instanceof byte[]) ? Bytes.toBytes((String)o) : o;
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toUTF8(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toUTF8(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] b = (byte[])((byte[])super.deserialize(w));
        return b == null ? b : Bytes.toString(b);
    }
}