package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;

import java.io.IOException;

public class FloatWritableRowKey extends RowKey {
    private static final int NULL = 0;
    private FloatWritable fw;

    public FloatWritableRowKey() {
    }

    public Class<?> getSerializedClass() {
        return FloatWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        return o == null && !this.terminate() ? 0 : 4;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        int j;
        if (o == null) {
            if (!this.terminate()) {
                return;
            }

            j = 0;
        } else {
            j = Float.floatToIntBits(((FloatWritable)o).get());
            j = (j ^ (j >> 31 | -2147483648)) + 1;
        }

        Bytes.putInt(b, offset, j ^ this.order.mask());
        RowKeyUtils.seek(w, 4);
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        if (w.getLength() > 0) {
            RowKeyUtils.seek(w, 4);
        }
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] s = w.get();
        int offset = w.getOffset();
        if (w.getLength() <= 0) {
            return null;
        } else {
            FloatWritable var5;
            try {
                int j = Bytes.toInt(s, offset) ^ this.order.mask();
                if (j != 0) {
                    if (this.fw == null) {
                        this.fw = new FloatWritable();
                    }

                    --j;
                    j ^= ~j >> 31 | -2147483648;
                    this.fw.set(Float.intBitsToFloat(j));
                    var5 = this.fw;
                    return var5;
                }

                var5 = null;
            } finally {
                RowKeyUtils.seek(w, 4);
            }

            return var5;
        }
    }
}
