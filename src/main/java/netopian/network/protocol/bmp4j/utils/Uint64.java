
package netopian.network.protocol.bmp4j.utils;

import java.math.BigInteger;
import java.util.Objects;

public final class Uint64 extends Number implements Comparable<Uint64> {
    private static final long serialVersionUID = 5695469794937991749L;

    private static final String MAX_VALUE_STR = Long.toUnsignedString(-1L);

    private static final int CACHE_SIZE = 256;

    private static final Uint64[] CACHE;

    private final long value;

    static {
        CACHE = new Uint64[CACHE_SIZE];
        for (int i = 0; i < CACHE.length; ++i) {
            CACHE[i] = new Uint64(i);
        }
    }

    private Uint64(long value) {
        this.value = value;
    }

    public Uint64(Uint64 other) {
        this(other.value);
    }

    private static Uint64 instanceFor(long value) {
        return value >= 0L && value < (long) CACHE.length ? CACHE[(int) value] : new Uint64(value);
    }

    public static Uint64 fromLongBits(long bits) {
        return instanceFor(bits);
    }

    public static Uint64 valueOf(byte byteVal) {
        Prediction.checkRange(byteVal, MAX_VALUE_STR);
        return instanceFor((long) byteVal);
    }

    public static Uint64 valueOf(short shortVal) {
        Prediction.checkRange(shortVal, MAX_VALUE_STR);
        return instanceFor(shortVal);
    }

    public static Uint64 valueOf(int intVal) {
        Prediction.checkRange(intVal, MAX_VALUE_STR);
        return instanceFor(intVal);
    }

    public static Uint64 valueOf(long longVal) {
        Prediction.checkRange(longVal, MAX_VALUE_STR);
        return instanceFor(longVal);
    }

    public static Uint64 valueOf(Uint8 uint) {
        return instanceFor(uint.shortValue());
    }

    public static Uint64 valueOf(Uint16 uint) {
        return instanceFor(uint.intValue());
    }

    public static Uint64 valueOf(Uint32 uint) {
        return instanceFor(uint.longValue());
    }

    public static Uint64 valueOf(BigInteger bigInt) {
        if (bigInt.signum() >= 0 && bigInt.bitLength() <= 64) {
            return instanceFor(bigInt.longValue());
        } else {
            throw new IllegalArgumentException("Invalid range: " + bigInt + ", expected: [[0..18446744073709551615]].");
        }
    }

    public static Uint64 valueOf(String string) {
        return valueOf(string, 10);
    }

    public static Uint64 valueOf(String string, int radix) {
        return instanceFor(Long.parseUnsignedLong((String) Objects.requireNonNull(string), radix));
    }

    @Override
    public final int intValue() {
        return (int) value;
    }

    @Override
    public final long longValue() {
        return value;
    }

    public final BigInteger unsignedLongValue() {
        return new BigInteger(Long.toUnsignedString(value));
    }

    @Override
    public final float floatValue() {
        return Float.parseFloat(Long.toUnsignedString(value));
    }

    @Override
    public final double doubleValue() {
        return Double.parseDouble(Long.toUnsignedString(value));
    }

    @Override
    public final int compareTo(Uint64 o) {
        return Long.compareUnsigned(value, o.value);
    }

    public final Uint8 toUint8() {
        if ((value & -256L) != 0L) {
            Prediction.throwIAE(value, 255L);
        }

        return Uint8.fromByteBits((byte) ((int) value));
    }

    public final Uint16 toUint16() {
        if ((value & -65536L) != 0L) {
            Prediction.throwIAE(value, 65535L);
        }

        return Uint16.fromShortBits((short) ((int) value));
    }

    public final Uint32 toUint32() {
        if ((value & -4294967296L) != 0L) {
            Prediction.throwIAE(value, 4294967295L);
        }

        return Uint32.fromIntBits((int) value);
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj instanceof Uint64 && value == ((Uint64) obj).value;
    }

    public final boolean equals(Uint64 obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj != null && value == obj.value;
    }

    @Override
    public final String toString() {
        return Long.toUnsignedString(longValue(), 16);
    }
}