package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;

import java.io.IOException;

public class FixedBytesWritableRowKey extends RowKey {
    private int length;

    public FixedBytesWritableRowKey(int length) {
        this.length = length;
    }

    public Class<?> getSerializedClass() {
        return BytesWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        return this.length;
    }

    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] bytesToWriteIn = w.get();
        int writeOffset = w.getOffset();
        BytesWritable bytesWritableToWrite = (BytesWritable)o;
        int srcLen = bytesWritableToWrite.getLength();
        byte[] bytesToWrite = bytesWritableToWrite.getBytes();
        if (srcLen != this.length) {
            throw new IllegalArgumentException("can only serialize byte arrays of length " + this.length + ", not " + srcLen);
        } else {
            byte[] maskedBytesToWrite = this.maskAll(bytesToWrite, this.order, 0, srcLen);
            Bytes.putBytes(bytesToWriteIn, writeOffset, maskedBytesToWrite, 0, srcLen);
            RowKeyUtils.seek(w, srcLen);
        }
    }

    private byte[] maskAll(byte[] bytes, Order order, int offset, int length) {
        if (order.mask() == 0) {
            return bytes;
        } else {
            byte[] masked = new byte[bytes.length];

            for(int i = offset; i < length + offset; ++i) {
                masked[i] = (byte)(bytes[i] ^ order.mask());
            }

            return masked;
        }
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        RowKeyUtils.seek(w, this.length);
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        int offset = w.getOffset();
        byte[] serialized = w.get();
        byte[] unmasked = this.maskAll(serialized, this.order, offset, this.length);
        RowKeyUtils.seek(w, this.length);
        BytesWritable result = new BytesWritable();
        result.set(unmasked, offset, this.length);
        return result;
    }
}
