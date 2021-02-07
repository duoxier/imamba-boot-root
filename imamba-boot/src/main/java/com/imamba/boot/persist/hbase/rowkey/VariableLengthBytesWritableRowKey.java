package com.imamba.boot.persist.hbase.rowkey;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.shaded.com.google.common.primitives.UnsignedBytes;
import org.apache.hadoop.io.BytesWritable;

import java.io.IOException;

public class VariableLengthBytesWritableRowKey extends RowKey {
    private static final byte NULL = 0;
    private static final byte TERMINATOR_NIBBLE = 1;
    private static final byte TWO_TERMINATOR_NIBBLES = 17;
    private static final char FILLER = 'f';
    private static final byte FILLER_NIBBLE = 2;
    private final int fixedPrefixLength;
    private static byte[] CUSTOMIZED_BCD_ENC_LOOKUP = new byte[]{3, 4, 5, 6, 7, 9, 10, 12, 14, 15};
    private static byte[] CUSTOMIZED_BCD_DEC_LOOKUP = new byte[]{-1, -1, -1, 0, 1, 2, 3, 4, -1, 5, 6, -1, 7, -1, 8, 9};

    public VariableLengthBytesWritableRowKey() {
        this(0);
    }

    public VariableLengthBytesWritableRowKey(int fixedPrefixLength) {
        if (fixedPrefixLength < 0) {
            throw new IllegalArgumentException("fixed prefix length can not be < 0");
        } else {
            this.fixedPrefixLength = fixedPrefixLength;
        }
    }

    public Class<?> getSerializedClass() {
        return BytesWritable.class;
    }

    public int getSerializedLength(Object o) throws IOException {
        if (o == null) {
            return this.terminate() ? this.fixedPrefixLength + 1 : this.fixedPrefixLength;
        } else {
            BytesWritable input = (BytesWritable)o;
            return this.fixedPrefixLength + this.getSerializedLength(this.toStringRepresentation(input.getBytes(), this.fixedPrefixLength, input.getLength() - this.fixedPrefixLength));
        }
    }

    private int getSerializedLength(String s) {
        if (this.terminate()) {
            return (s.length() + 2) / 2;
        } else {
            return s.length() == 0 ? 1 : (s.length() + 1) / 2;
        }
    }

    public void serialize(Object o, ImmutableBytesWritable bytesWritable) throws IOException {
        byte[] bytesToWriteIn = bytesWritable.get();
        int offset = bytesWritable.getOffset();
        if (o == null) {
            if (this.fixedPrefixLength > 0) {
                throw new IllegalStateException("excepted at least " + this.fixedPrefixLength + " bytes to write");
            }

            if (this.terminate()) {
                bytesToWriteIn[offset] = this.mask((byte)0);
                RowKeyUtils.seek(bytesWritable, 1);
            }
        } else {
            BytesWritable input = (BytesWritable)o;
            if (this.fixedPrefixLength > input.getLength()) {
                throw new IllegalStateException("excepted at least " + this.fixedPrefixLength + " bytes to write");
            }

            this.encodeFixedPrefix(input.getBytes(), bytesWritable);
            this.encodedCustomizedReversedPackedBcd(this.toStringRepresentation(input.getBytes(), this.fixedPrefixLength, input.getLength() - this.fixedPrefixLength), bytesWritable);
        }

    }

    private void encodeFixedPrefix(byte[] input, ImmutableBytesWritable bytesWritable) {
        byte[] output = bytesWritable.get();
        int offset = bytesWritable.getOffset();

        for(int i = 0; i < this.fixedPrefixLength; ++i) {
            output[offset + i] = this.mask(input[i]);
        }

        RowKeyUtils.seek(bytesWritable, this.fixedPrefixLength);
    }

    private String toStringRepresentation(byte[] bytes, int offset, int length) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            byte aByte = bytes[offset + i];
            result.append(this.prependZeroes(3, "" + UnsignedBytes.toInt(aByte)));
        }

        return result.toString();
    }

    private String prependZeroes(int totalLength, String string) {
        if (string.length() >= totalLength) {
            return string;
        } else {
            StringBuilder zeroes = new StringBuilder(totalLength - string.length());

            for(int i = 0; i < totalLength - string.length(); ++i) {
                zeroes.append("0");
            }

            return zeroes.toString() + string;
        }
    }

    private byte[] fromStringRepresentation(String string) {
        byte[] result = new byte[string.length() / 3];
        char[] digits = string.toCharArray();

        for(int i = 0; i < result.length; ++i) {
            int digitIdx = i * 3;
            StringBuilder singleByteBcdString = new StringBuilder();

            for(int j = 0; j < 3; ++j) {
                singleByteBcdString.append(digits[digitIdx + j] == 'f' ? "" : digits[digitIdx + j]);
            }

            result[i] = (byte)Integer.parseInt(singleByteBcdString.toString());
        }

        return result;
    }

    public void skip(ImmutableBytesWritable bytesWritable) throws IOException {
        if (bytesWritable.getLength() > 0) {
            byte[] bytes = bytesWritable.get();
            int offset = bytesWritable.getOffset();
            int len = bytesWritable.getLength();
            RowKeyUtils.seek(bytesWritable, this.fixedPrefixLength + this.getBcdEncodedLength(bytes, offset + this.fixedPrefixLength, len - this.fixedPrefixLength));
        }
    }

    protected int getBcdEncodedLength(byte[] bytes, int offset, int len) {
        int i = 0;

        while(i < len) {
            byte c = this.mask(bytes[offset + i++]);
            if ((c & 15) == 1) {
                break;
            }
        }

        return i;
    }

    public Object deserialize(ImmutableBytesWritable bytesWritable) throws IOException {
        int length = bytesWritable.getLength();
        if (length <= 0 && this.fixedPrefixLength == 0) {
            return null;
        } else {
            int offset = bytesWritable.getOffset();
            int variableLengthSuffixOffset = offset + this.fixedPrefixLength;
            int variableLengthSuffixLength = length - this.fixedPrefixLength;
            byte[] fixedLengthPrefix = this.decodeFixedPrefix(bytesWritable);
            byte[] variableLengthSuffix = this.fromStringRepresentation(this.decodeCustomizedReversedPackedBcd(bytesWritable, variableLengthSuffixOffset, variableLengthSuffixLength));
            return new BytesWritable(merge(fixedLengthPrefix, variableLengthSuffix));
        }
    }

    private static byte[] merge(byte[] array1, byte[] array2) {
        byte[] merged = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, merged, 0, array1.length);
        System.arraycopy(array2, 0, merged, array1.length, array2.length);
        return merged;
    }

    private byte[] decodeFixedPrefix(ImmutableBytesWritable input) {
        byte[] output = new byte[this.fixedPrefixLength];
        byte[] inputBytes = input.get();
        int offset = input.getOffset();

        for(int i = 0; i < this.fixedPrefixLength; ++i) {
            output[i] = this.mask(inputBytes[offset + i]);
        }

        RowKeyUtils.seek(input, this.fixedPrefixLength);
        return output;
    }

    void encodedCustomizedReversedPackedBcd(String decimalDigits, ImmutableBytesWritable bytesWritable) {
        byte[] bytes = bytesWritable.get();
        int offset = bytesWritable.getOffset();
        int encodedLength = this.getSerializedLength(decimalDigits);
        char[] digits = decimalDigits.toCharArray();

        for(int i = 0; i < encodedLength; ++i) {
            byte bcd = 17;
            int digitsIdx = 2 * i;
            boolean firstNibbleWritten = false;
            if (digitsIdx < digits.length) {
                bcd = (byte)(this.lookupDigit(digits[digitsIdx]) << 4);
                firstNibbleWritten = true;
            }

            ++digitsIdx;
            if (digitsIdx < digits.length) {
                bcd |= this.lookupDigit(digits[digitsIdx]);
            } else if (firstNibbleWritten) {
                bcd = (byte)(bcd | 1);
            }

            bytes[offset + i] = this.mask(bcd);
        }

        RowKeyUtils.seek(bytesWritable, encodedLength);
    }

    private byte lookupDigit(char digit) {
        return digit != 'f' ? CUSTOMIZED_BCD_ENC_LOOKUP[Character.digit(digit, 10)] : 2;
    }

    String decodeCustomizedReversedPackedBcd(ImmutableBytesWritable bytesWritable, int offset, int length) {
        int i = 0;
        byte[] bytes = bytesWritable.get();
        StringBuilder sb = new StringBuilder();

        while(i < length) {
            byte c = this.mask(bytes[offset + i++]);
            if (this.addDigit((byte)(c >>> 4 & 15), sb) || this.addDigit((byte)(c & 15), sb)) {
                break;
            }
        }

        RowKeyUtils.seek(bytesWritable, i);
        return sb.toString();
    }

    protected boolean addDigit(byte bcd, StringBuilder sb) {
        if (bcd == 1) {
            return true;
        } else {
            if (bcd != 2) {
                sb.append(CUSTOMIZED_BCD_DEC_LOOKUP[bcd]);
            } else {
                sb.append('f');
            }

            return false;
        }
    }
}
