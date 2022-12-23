
package netopian.network.protocol.bmp4j.utils;

import java.util.Objects;

public final class Uint16 extends Number implements Comparable<Uint16> {

    private static final long serialVersionUID = 548756731108797541L;

    private static final int MAX_VALUE = 65535;

    private static final int CACHE_SIZE = 256;

    private static final Uint16[] CACHE;

    private final short value;

    static {
        CACHE = new Uint16[CACHE_SIZE];
        for (int i = 0; i < CACHE.length; ++i) {
            CACHE[i] = new Uint16((short) i);
        }
    }

    private Uint16(short value) {
        this.value = value;
    }

    public Uint16(Uint16 other) {
        value = other.value;
    }

    private static Uint16 instanceFor(short value) {
        int slot = Short.toUnsignedInt(value);
        return slot < CACHE.length ? CACHE[slot] : new Uint16(value);
    }

    public static Uint16 fromShortBits(short bits) {
        return instanceFor(bits);
    }

    public static Uint16 valueOf(byte byteVal) {
        Prediction.checkRange(byteVal, MAX_VALUE);
        return instanceFor(byteVal);
    }

    public static Uint16 valueOf(short shortVal) {
        Prediction.checkRange(shortVal, MAX_VALUE);
        return instanceFor(shortVal);
    }

    public static Uint16 valueOf(int intVal) {
        Prediction.checkRange(intVal, MAX_VALUE);
        return instanceFor((short) intVal);
    }

    public static Uint16 valueOf(long longVal) {
        Prediction.checkRange(longVal, MAX_VALUE);
        return instanceFor((short) ((int) longVal));
    }

    public static Uint16 valueOf(Uint8 uint) {
        return instanceFor(uint.shortValue());
    }

    public static Uint16 valueOf(Uint32 uint) {
        return valueOf(uint.longValue());
    }

    public static Uint16 valueOf(Uint64 uint) {
        return valueOf(uint.longValue());
    }

    public static Uint16 valueOf(String string) {
        return valueOf(string, 10);
    }

    public static Uint16 valueOf(String string, int radix) {
        return valueOf(Integer.parseInt((String) Objects.requireNonNull(string), radix));
    }

    @Override
    public final short shortValue() {
        return value;
    }

    @Override
    public final int intValue() {
        return Short.toUnsignedInt(value);
    }

    @Override
    public final long longValue() {
        return Short.toUnsignedLong(value);
    }

    @Override
    public final float floatValue() {
        return (float) intValue();
    }

    @Override
    public final double doubleValue() {
        return intValue();
    }

    @Override
    public final int compareTo(Uint16 o) {
        return intValue() - o.intValue();
    }

    public final Uint8 toUint8() {
        return Uint8.valueOf(intValue());
    }

    public final Uint32 toUint32() {
        return Uint32.fromIntBits(intValue());
    }

    public final Uint64 toUint64() {
        return Uint64.fromLongBits(longValue());
    }

    @Override
    public final int hashCode() {
        return Short.hashCode(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj instanceof Uint16 && value == ((Uint16) obj).value;
    }

    public final boolean equals(Uint16 obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj != null && value == obj.value;
    }

    @Override
    public final String toString() {
        return Integer.toString(intValue(), 16);
    }
}
