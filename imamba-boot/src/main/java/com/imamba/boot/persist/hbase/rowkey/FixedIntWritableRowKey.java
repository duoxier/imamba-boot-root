package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;

public class FixedIntWritableRowKey extends RowKey {
    private IntWritable iw;

    public FixedIntWritableRowKey() {
    }

    public Class<?> getSerializedClass() {
        return IntWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        return 4;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        int i = ((IntWritable)o).get();
        Bytes.putInt(b, offset, i ^ -2147483648 ^ this.order.mask());
        RowKeyUtils.seek(w, 4);
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        RowKeyUtils.seek(w, 4);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        int offset = w.getOffset();
        byte[] s = w.get();
        int i = Bytes.toInt(s, offset) ^ -2147483648 ^ this.order.mask();
        RowKeyUtils.seek(w, 4);
        if (this.iw == null) {
            this.iw = new IntWritable();
        }

        this.iw.set(i);
        return this.iw;
    }
}