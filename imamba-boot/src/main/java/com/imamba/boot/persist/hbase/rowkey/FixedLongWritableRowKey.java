package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class FixedLongWritableRowKey extends RowKey {
    private LongWritable lw;

    public FixedLongWritableRowKey() {
    }

    public Class<?> getSerializedClass() {
        return LongWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        return 8;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        long l = ((LongWritable)o).get();
        Bytes.putLong(b, offset, l ^ -9223372036854775808L ^ (long)this.order.mask());
        RowKeyUtils.seek(w, 8);
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        RowKeyUtils.seek(w, 8);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        int offset = w.getOffset();
        byte[] s = w.get();
        long l = Bytes.toLong(s, offset) ^ -9223372036854775808L ^ (long)this.order.mask();
        RowKeyUtils.seek(w, 8);
        if (this.lw == null) {
            this.lw = new LongWritable();
        }

        this.lw.set(l);
        return this.lw;
    }
}