package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Writable;

import java.io.IOException;

public abstract class AbstractVarIntRowKey extends RowKey {
    protected static final byte NULL = 0;
    protected static final int HEADER_EXT_LENGTH_BIAS = 3;
    private final byte HEADER_SINGLE;
    private final byte HEADER_DOUBLE;
    private final int HEADER_SINGLE_DATA_BITS;
    private final int HEADER_DOUBLE_DATA_BITS;
    private final int HEADER_EXT_DATA_BITS;
    private final int HEADER_EXT_LENGTH_BITS;
    protected Writable lw;
    protected int reservedBits;
    protected int reservedValue;

    protected AbstractVarIntRowKey(byte headerSingle, int headerSingleDataBits, byte headerDouble, int headerDoubleDataBits, int headerExtLengthBits, int headerExtDataBits) {
        this.HEADER_SINGLE = headerSingle;
        this.HEADER_SINGLE_DATA_BITS = headerSingleDataBits;
        this.HEADER_DOUBLE = headerDouble;
        this.HEADER_DOUBLE_DATA_BITS = headerDoubleDataBits;
        this.HEADER_EXT_LENGTH_BITS = headerExtLengthBits;
        this.HEADER_EXT_DATA_BITS = headerExtDataBits;
    }

    abstract Writable createWritable();

    abstract void setWritable(long var1, Writable var3);

    abstract long getWritable(Writable var1);

    public int getReservedBits() {
        return this.reservedBits;
    }

    public int getMaxReservedBits() {
        return this.HEADER_EXT_DATA_BITS;
    }

    public AbstractVarIntRowKey setReservedBits(int reservedBits) {
        if (reservedBits > this.getMaxReservedBits()) {
            throw new IndexOutOfBoundsException("Requested " + reservedBits + " reserved bits but only " + this.getMaxReservedBits() + " permitted");
        } else {
            this.reservedBits = reservedBits;
            return this;
        }
    }

    public AbstractVarIntRowKey setReservedValue(int reservedValue) {
        this.reservedValue = reservedValue & (1 << this.reservedBits) - 1;
        return this;
    }

    public int getReservedValue() {
        return this.reservedValue;
    }

    abstract long getSign(long var1);

    protected byte readByte(long x, int byteOffset) {
        return byteOffset >= 8 ? (byte)((int)(this.getSign(x) >> 31)) : (byte)((int)(x >> byteOffset * 8));
    }

    protected long writeByte(byte b, long x, int byteOffset) {
        if (byteOffset >= 8) {
            return x;
        } else {
            return this.getSign(x) != 0L ? x & ~(((long)(~b) & 255L) << byteOffset * 8) : x | ((long)b & 255L) << byteOffset * 8;
        }
    }

    public int getSerializedLength(Object o) throws IOException {
        if (o == null) {
            return this.terminate() ? 1 : 0;
        } else {
            long x = this.getWritable((Writable)o);
            long diffBits = x ^ this.getSign(x) >> 63;
            int numBits = 64 - Long.numberOfLeadingZeros(diffBits);
            if (numBits <= this.HEADER_SINGLE_DATA_BITS - this.reservedBits) {
                return 1;
            } else {
                return numBits <= this.HEADER_DOUBLE_DATA_BITS - this.reservedBits + 8 ? 2 : 1 + (numBits - this.HEADER_EXT_DATA_BITS + this.reservedBits + 7 >>> 3);
            }
        }
    }

    protected byte getNull() {
        int nullHeader = this.mask((byte)0) & 255 >>> this.reservedBits;
        return (byte)(nullHeader | this.reservedValue << 8 - this.reservedBits);
    }

    protected boolean isNull(byte h) {
        return (this.mask(h) & 255 >>> this.reservedBits) == 0;
    }

    protected abstract byte initHeader(boolean var1);

    protected byte serializeNonNullHeader(byte h) {
        return h;
    }

    protected int getNumHeaderDataBits(int length) {
        if (length == 1) {
            return this.HEADER_SINGLE_DATA_BITS - this.reservedBits;
        } else {
            return length == 2 ? this.HEADER_DOUBLE_DATA_BITS - this.reservedBits : this.HEADER_EXT_DATA_BITS - this.reservedBits;
        }
    }

    protected byte toHeader(boolean sign, int length, byte data) {
        int b = initHeader(sign),
                negSign = sign ? 0 : -1;

        if (length == 1) {
            b |= (~negSign & HEADER_SINGLE);
        } else if (length == 2) {
            b |= (negSign & HEADER_SINGLE) | (~negSign & HEADER_DOUBLE);
        } else {
            int encodedLength = (length - HEADER_EXT_LENGTH_BIAS) ^ ~negSign;
            encodedLength &= (1 << HEADER_EXT_LENGTH_BITS) - 1;
            encodedLength <<= HEADER_EXT_DATA_BITS;
            b |= (negSign & (HEADER_SINGLE | HEADER_DOUBLE)) | encodedLength;
        }

        data &= (1 << getNumHeaderDataBits(length)) - 1;
        b = serializeNonNullHeader((byte) ((b >>> reservedBits) | data));
        b = mask((byte) b) & (0xff >>> reservedBits);
        return (byte) (b | (reservedValue << Byte.SIZE - reservedBits));
    }


    public void serialize(Object o, ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        if (o == null) {
            if (this.terminate()) {
                b[offset] = this.getNull();
                RowKeyUtils.seek(w, 1);
            }

        } else {
            long x = this.getWritable((Writable)o);
            int length = this.getSerializedLength((Writable)o);
            b[offset] = this.toHeader(this.getSign(x) != 0L, length, this.readByte(x, length - 1));

            for(int i = 1; i < length; ++i) {
                b[offset + i] = this.mask(this.readByte(x, length - i - 1));
            }

            RowKeyUtils.seek(w, length);
        }
    }

    protected abstract byte getSign(byte var1);

    protected byte deserializeNonNullHeader(byte h) {
        return h;
    }

    protected int getVarIntLength(byte h) {
        int negSign = ~this.getSign(h) >> 31;
        if (((h ^ negSign) & this.HEADER_SINGLE) != 0) {
            return 1;
        } else if (((h ^ negSign) & this.HEADER_DOUBLE) != 0) {
            return 2;
        } else {
            int length = (h ^ ~negSign) >>> this.HEADER_EXT_DATA_BITS;
            length &= (1 << this.HEADER_EXT_LENGTH_BITS) - 1;
            return length + 3;
        }
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        if (w.getLength() > 0) {
            if (this.isNull(b[offset])) {
                RowKeyUtils.seek(w, 1);
            } else {
                byte h = this.deserializeNonNullHeader(this.mask(b[offset]));
                RowKeyUtils.seek(w, this.getVarIntLength((byte)(h << this.reservedBits)));
            }

        }
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        if (w.getLength() <= 0) {
            return null;
        } else if (this.isNull(b[offset])) {
            RowKeyUtils.seek(w, 1);
            return null;
        } else {
            byte h = (byte)(this.deserializeNonNullHeader(this.mask(b[offset])) << this.reservedBits);
            int length = this.getVarIntLength(h);
            long x = (long)this.getSign(h) >> 63;
            byte d = (byte)((int)(x << this.getNumHeaderDataBits(length)));
            d |= (byte)(h >>> this.reservedBits & (1 << this.getNumHeaderDataBits(length)) - 1);
            x = this.writeByte(d, x, length - 1);

            for(int i = 1; i < length; ++i) {
                x = this.writeByte(this.mask(b[offset + i]), x, length - i - 1);
            }

            RowKeyUtils.seek(w, length);
            if (this.lw == null) {
                this.lw = this.createWritable();
            }

            this.setWritable(x, this.lw);
            return this.lw;
        }
    }
}