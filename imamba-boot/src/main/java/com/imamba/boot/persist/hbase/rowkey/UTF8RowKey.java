package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import java.io.IOException;

public class UTF8RowKey extends RowKey {
    private static final byte NULL = 0;
    private static final byte TERMINATOR = 1;

    public UTF8RowKey() {
    }

    public Class<?> getSerializedClass() {
        return byte[].class;
    }

    public int getSerializedLength(Object o) throws IOException {
        int term = this.terminate() ? 1 : 0;
        return o == null ? term : Math.max(((byte[])((byte[])o)).length + term, 1);
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        if (o == null) {
            if (this.terminate()) {
                b[offset] = this.mask((byte)0);
                RowKeyUtils.seek(w, 1);
            }

        } else {
            byte[] s = (byte[])((byte[])o);
            int len = s.length;

            for(int i = 0; i < len; ++i) {
                b[offset + i] = this.mask((byte)(s[i] + 2));
            }

            boolean terminated = this.terminate() || len == 0;
            if (terminated) {
                b[offset + len] = this.mask((byte)1);
            }

            RowKeyUtils.seek(w, len + (terminated ? 1 : 0));
        }
    }

    protected int getUTF8RowKeyLength(ImmutableBytesWritable w) {
        byte[] b = w.get();
        int offset = w.getOffset();
        int len = w.getLength();
        if (len <= 0) {
            return 0;
        } else if (b[offset] == this.mask((byte)0)) {
            return 1;
        } else {
            int i = 0;

            while(i < len && b[offset + i++] != this.mask((byte)1)) {
            }

            return i;
        }
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        RowKeyUtils.seek(w, this.getUTF8RowKeyLength(w));
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] s = w.get();
        int offset = w.getOffset();
        if (w.getLength() <= 0) {
            return null;
        } else {
            int len = this.getUTF8RowKeyLength(w);

            Object var5;
            try {
                if (s[offset] != this.mask((byte)0)) {
                    if (s[offset] == this.mask((byte)1)) {
                        byte[] var12 = RowKeyUtils.EMPTY;
                        return var12;
                    }

                    boolean terminated = s[offset + len - 1] == this.mask((byte)1);
                    byte[] b = new byte[len - (terminated ? 1 : 0)];

                    for(int i = 0; i < b.length; ++i) {
                        b[i] = (byte)(this.mask(s[offset + i]) - 2);
                    }

                    byte[] var13 = b;
                    return var13;
                }

                var5 = null;
            } finally {
                RowKeyUtils.seek(w, len);
            }

            return var5;
        }
    }
}
