package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class TextRowKey extends UTF8RowKey {
    private Text t;

    public TextRowKey() {
    }

    public Class<?> getSerializedClass() {
        return Text.class;
    }

    protected Object toUTF8(Object o) {
        return o != null && !(o instanceof byte[]) ? RowKeyUtils.toBytes((Text)o) : o;
    }

    public int getSerializedLength(Object o) throws IOException {
        return super.getSerializedLength(this.toUTF8(o));
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        super.serialize(this.toUTF8(o), w);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] b = (byte[])((byte[])super.deserialize(w));
        if (b == null) {
            return b;
        } else {
            if (this.t == null) {
                this.t = new Text();
            }

            this.t.set(b);
            return this.t;
        }
    }
}