package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalRowKey extends RowKey {
    protected static final byte HEADER_NULL = 0;
    protected static final byte HEADER_NEGATIVE = 1;
    protected static final byte HEADER_ZERO = 2;
    protected static final byte HEADER_POSITIVE = 3;
    protected static final int HEADER_BITS = 2;
    protected LongWritable lw;
    protected BigDecimalRowKey.ExponentRowKey expKey = new BigDecimalRowKey.ExponentRowKey();
    protected byte signMask;

    public BigDecimalRowKey() {
        this.expKey.setReservedBits(2).setTermination(Termination.MUST);
    }

    public RowKey setOrder(Order order) {
        this.expKey.setOrder(order);
        return super.setOrder(order);
    }

    protected byte getSignMask() {
        return this.signMask;
    }

    protected void resetSignMask() {
        this.setSignMask((byte)0);
    }

    protected void setSignMask(byte signMask) {
        this.signMask = signMask;
    }

    protected byte mask(byte b) {
        return (byte)(b ^ this.order.mask() ^ this.signMask);
    }

    public Class<?> getSerializedClass() {
        return BigDecimal.class;
    }

    protected int getSerializedLength(String s) {
        return s.length() + (this.terminate() ? 2 : 1) >>> 1;
    }

    protected void serializeBCD(String s, ImmutableBytesWritable w) {
        byte[] b = w.get();
        int offset = w.getOffset();
        int strLength = s.length();
        int bcdLength = this.getSerializedLength(s);

        for(int i = 0; i < bcdLength; ++i) {
            byte bcd = 0;
            int strPos = 2 * i;
            if (strPos < strLength) {
                bcd = (byte)(1 + Character.digit(s.charAt(strPos), 10) << 4);
            }

            ++strPos;
            if (strPos < strLength) {
                bcd |= (byte)(1 + Character.digit(s.charAt(strPos), 10));
            }

            b[offset + i] = this.mask(bcd);
        }

        RowKeyUtils.seek(w, bcdLength);
    }

    protected String getDecimalString(BigInteger i) {
        String s = i.toString();
        return i.signum() >= 0 ? s : s.substring(1);
    }

    public int getSerializedLength(Object o) throws IOException {
        if (o == null) {
            return this.terminate() ? this.expKey.getSerializedLength((Object)null) : 0;
        } else {
            BigDecimal d = ((BigDecimal)o).stripTrailingZeros();
            BigInteger i = d.unscaledValue();
            if (i.signum() == 0) {
                return this.expKey.getSerializedLength((Object)null);
            } else {
                String s = this.getDecimalString(i);
                if (this.lw == null) {
                    this.lw = new LongWritable();
                }

                this.lw.set((long)s.length() + (long)(-d.scale()) - 1L);
                return this.expKey.getSerializedLength(this.lw) + this.getSerializedLength(s);
            }
        }
    }

    public void serialize(Object o, ImmutableBytesWritable w)
            throws IOException {
        resetSignMask();

        if (o == null) {
            if (terminate()) {
                expKey.setReservedValue(mask(HEADER_NULL));
                expKey.serialize(null, w);
            }
            return;
        }

        BigDecimal d = ((BigDecimal) o).stripTrailingZeros();
        BigInteger i = d.unscaledValue();
        if (i.signum() == 0) {
            expKey.setReservedValue(mask(HEADER_ZERO));
            expKey.serialize(null, w);
            return;
        }

        byte header = i.signum() < 0 ? HEADER_NEGATIVE : HEADER_POSITIVE;
        expKey.setReservedValue(mask(header));

        String s = getDecimalString(i);
        /* Adjusted exponent = precision + scale - 1 */
        long precision = s.length(),
                exp = precision + -d.scale() - 1L;
        if (lw == null)
            lw = new LongWritable();
        lw.set(exp);

        setSignMask((byte) (i.signum() >> Integer.SIZE - 1));
        expKey.serialize(lw, w);
        serializeBCD(s, w);
    }


    protected boolean addDigit(byte bcd, StringBuilder sb) {
        if (bcd == 0) {
            return true;
        } else {
            sb.append((char)(48 + bcd - 1));
            return false;
        }
    }

    protected String deserializeBCD(ImmutableBytesWritable w) {
        byte[] b = w.get();
        int offset = w.getOffset();
        int len = w.getLength();
        int i = 0;
        StringBuilder sb = new StringBuilder();

        while(i < len) {
            byte c = this.mask(b[offset + i++]);
            if (this.addDigit((byte)(c >>> 4 & 15), sb) || this.addDigit((byte)(c & 15), sb)) {
                break;
            }
        }

        RowKeyUtils.seek(w, i);
        return sb.toString();
    }

    protected int getBCDEncodedLength(ImmutableBytesWritable w) {
        byte[] b = w.get();
        int offset = w.getOffset();
        int len = w.getLength();
        int i = 0;

        while(i < len) {
            byte c = this.mask(b[offset + i++]);
            if ((c & 240) == 0 || (c & 15) == 0) {
                break;
            }
        }

        return i;
    }

    protected byte deserializeHeader(byte b) {
        this.resetSignMask();
        byte h = (byte)((this.mask(b) & 255) >>> 6);
        this.setSignMask((byte)(h == 1 ? -1 : 0));
        return h;
    }

    public void skip(ImmutableBytesWritable w) throws IOException {
        if (w.getLength() > 0) {
            byte b = w.get()[w.getOffset()];
            this.deserializeHeader(b);
            this.expKey.skip(w);
            if (!this.expKey.isNull(b)) {
                RowKeyUtils.seek(w, this.getBCDEncodedLength(w));
            }
        }
    }

    public Object deserialize(ImmutableBytesWritable w) throws IOException {
        byte[] b = w.get();
        int offset = w.getOffset();
        if (w.getLength() <= 0) {
            return null;
        } else {
            byte h = this.deserializeHeader(b[offset]);
            LongWritable o = (LongWritable)this.expKey.deserialize(w);
            if (o == null) {
                return h == 0 ? null : BigDecimal.ZERO;
            } else {
                long exp = o.get();
                String s = this.deserializeBCD(w);
                int precision = s.length();
                int scale = (int)(exp - (long)precision + 1L);
                BigInteger i = new BigInteger(h == 3 ? s : '-' + s);
                return new BigDecimal(i, -scale);
            }
        }
    }

    protected class ExponentRowKey extends IntWritableRowKey {
        protected ExponentRowKey() {
        }

        public Class<?> getSerializedClass() {
            return LongWritable.class;
        }

        Writable createWritable() {
            return new LongWritable();
        }

        void setWritable(long x, Writable w) {
            ((LongWritable)w).set(x);
        }

        long getWritable(Writable w) {
            return ((LongWritable)w).get();
        }

        protected byte mask(byte b) {
            return (byte)(b ^ this.order.mask() ^ BigDecimalRowKey.this.getSignMask());
        }
    }
}