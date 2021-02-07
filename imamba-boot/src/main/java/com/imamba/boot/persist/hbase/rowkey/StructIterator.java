package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StructIterator implements Iterator<Object> {
    private StructRowKey rowKey;
    private RowKey[] fields;
    private int fieldPos;
    private int origOffset;
    private int origLength;
    private ImmutableBytesWritable w;

    public StructIterator(StructRowKey rowKey) {
        this.setRowKey(rowKey);
    }

    public StructIterator(StructRowKey rowKey, ImmutableBytesWritable bytes) {
        this.setRowKey(rowKey);
        this.setBytes(bytes);
    }

    public StructIterator() {
    }

    public StructIterator setRowKey(StructRowKey rowKey) {
        this.rowKey = rowKey;
        this.fields = rowKey.getFields();
        return this;
    }

    public StructRowKey getRowKey() {
        return this.rowKey;
    }

    public StructIterator setBytes(ImmutableBytesWritable w) {
        this.w = w;
        this.fieldPos = 0;
        this.origOffset = w.getOffset();
        this.origLength = w.getLength();
        return this;
    }

    public ImmutableBytesWritable getBytes() {
        return this.w;
    }

    public void reset() {
        this.fieldPos = 0;
        if (this.w != null) {
            this.w.set(this.w.get(), this.origOffset, this.origLength);
        }

    }

    public void skip() throws IOException {
        this.fields[this.fieldPos++].skip(this.w);
    }

    public Object deserialize() throws IOException {
        return this.fields[this.fieldPos++].deserialize(this.w);
    }

    public boolean hasNext() {
        return this.fieldPos < this.fields.length;
    }

    public Object next() {
        try {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                return this.deserialize();
            }
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
