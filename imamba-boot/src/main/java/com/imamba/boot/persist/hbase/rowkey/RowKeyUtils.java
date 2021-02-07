package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;

import java.util.Arrays;

public class RowKeyUtils {
    public static final byte[] EMPTY = new byte[0];

    public RowKeyUtils() {
    }

    public static byte[] toBytes(byte[] b, int offset, int length) {
        if (offset == 0 && length == b.length) {
            return b;
        } else {
            return offset == 0 ? Arrays.copyOf(b, length) : Arrays.copyOfRange(b, offset, offset + length);
        }
    }

    public static byte[] toBytes(ImmutableBytesWritable w) {
        return toBytes(w.get(), w.getOffset(), w.getLength());
    }

    public static byte[] toBytes(Text t) {
        return toBytes(t.getBytes(), 0, t.getLength());
    }

    public static void seek(ImmutableBytesWritable w, int offset) {
        w.set(w.get(), w.getOffset() + offset, w.getLength() - offset);
    }
}