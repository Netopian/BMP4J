
package netopian.network.protocol.bmp4j.utils;

import java.util.Objects;

public final class Uint32 extends Number implements Comparable<Uint32> {

    private static final long serialVersionUID = 216874380193323740L;

    private static final long MAX_VALUE = 0xffffffffL;

    private static final int CACHE_SIZE = 256;

    private static final Uint32[] CACHE;

    private final int value;

    static {
        CACHE = new Uint32[CACHE_SIZE];
        for (int i = 0; i < CACHE.length; ++i) {
            CACHE[i] = new Uint32(i);
        }
    }

    Uint32(int value) {
        this.value = value;
    }

    public Uint32(Uint32 other) {
        value = other.value;
    }

    private static Uint32 instanceFor(int value) {
        long longSlot = Integer.toUnsignedLong(value);
        return longSlot < (long) CACHE.length ? CACHE[(int) longSlot] : new Uint32(value);
    }

    public static Uint32 fromIntBits(int bits) {
        return instanceFor(bits);
    }

    public static Uint32 valueOf(byte byteVal) {
        Prediction.checkRange(byteVal, MAX_VALUE);
        return instanceFor(byteVal);
    }

    public static Uint32 valueOf(short shortVal) {
        Prediction.checkRange(shortVal, MAX_VALUE);
        return instanceFor(shortVal);
    }

    public static Uint32 valueOf(int intVal) {
        Prediction.checkRange(intVal, MAX_VALUE);
        return instanceFor(intVal);
    }

    public static Uint32 valueOf(long longVal) {
        Prediction.checkRange(longVal, MAX_VALUE);
        return instanceFor((int) longVal);
    }

    public static Uint32 valueOf(Uint8 uint) {
        return instanceFor(uint.shortValue());
    }

    public static Uint32 valueOf(Uint16 uint) {
        return instanceFor(uint.intValue());
    }

    public static Uint32 valueOf(Uint64 uint) {
        return valueOf(uint.longValue());
    }

    public static Uint32 valueOf(String string) {
        return valueOf(string, 10);
    }

    public static Uint32 valueOf(String string, int radix) {
        return instanceFor(Integer.parseUnsignedInt((String) Objects.requireNonNull(string), radix));
    }

    @Override
    public final int intValue() {
        return value;
    }

    @Override
    public final long longValue() {
        return Integer.toUnsignedLong(value);
    }

    @Override
    public final float floatValue() {
        return (float) longValue();
    }

    @Override
    public final double doubleValue() {
        return (double) longValue();
    }

    @Override
    public final int compareTo(Uint32 o) {
        return Integer.compareUnsigned(value, o.value);
    }

    public final String toCanonicalString() {
        return Integer.toUnsignedString(value);
    }

    public final Uint8 toUint8() {
        return Uint8.valueOf(longValue());
    }

    public final Uint16 toUint16() {
        return Uint16.valueOf(longValue());
    }

    public final Uint64 toUint64() {
        return Uint64.fromLongBits(longValue());
    }

    @Override
    public final int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj instanceof Uint32 && value == ((Uint32) obj).value;
    }

    public final boolean equals(Uint32 obj) {
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
