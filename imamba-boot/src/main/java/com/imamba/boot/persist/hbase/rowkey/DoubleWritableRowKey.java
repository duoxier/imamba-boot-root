package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DoubleWritable;

import java.io.IOException;

public class DoubleWritableRowKey extends RowKey {
    private static final long NULL = 0L;
    private DoubleWritable dw;

    public DoubleWritableRowKey() {
    }

    public Class<?> getSerializedClass() {
        return DoubleWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        return o == null && !this.terminate() ? 0 : 8;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        long l;
        if (o == null) {
            if (!this.terminate()) {
                return;
            }

            l = 0L;
        } else {
            l = Double.doubleToLongBits(((DoubleWritable)o).get());
            l = (l ^ (l >> 63 | -9223372036854775808L)) + 1L;
        }

        Bytes.putLong(b, offset, l ^ (long)this.order.mask());
        RowKeyUtils.seek(w, 8);
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        if (w.getLength() > 0) {
            RowKeyUtils.seek(w, 8);
        }
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] s = w.get();
        int offset = w.getOffset();
        if (w.getLength() <= 0) {
            return null;
        } else {
            DoubleWritable var6;
            try {
                long l = Bytes.toLong(s, offset) ^ (long)this.order.mask();
                if (l == 0L) {
                    var6 = null;
                    return var6;
                }

                if (this.dw == null) {
                    this.dw = new DoubleWritable();
                }

                --l;
                l ^= ~l >> 63 | -9223372036854775808L;
                this.dw.set(Double.longBitsToDouble(l));
                var6 = this.dw;
            } finally {
                RowKeyUtils.seek(w, 8);
            }

            return var6;
        }
    }
}
