package netopian.network.protocol.bmp4j.utils;

public final class Uint8 extends Number implements Comparable<Uint8> {

    static final int MIN_VALUE = 0;

    static final int MAX_VALUE = 255;

    private static final long serialVersionUID = -9159801779616765462L;

    private static final Uint8[] CACHE = new Uint8[MAX_VALUE + 1];

    private final byte value;

    private Uint8(byte value) {
        this.value = value;
    }

    public Uint8(Uint8 other) {
        value = other.value;
    }

    private static Uint8 instanceFor(byte value) {
        int slot = Byte.toUnsignedInt(value);

        Uint8 ret = CACHE[slot];
        if (ret == null) {
            synchronized (CACHE) {
                ret = CACHE[slot];
                if (ret == null) {
                    ret = new Uint8(value);
                    CACHE[slot] = ret;
                }
            }
        }

        return ret;
    }

    public static Uint8 fromByteBits(byte bits) {
        return instanceFor(bits);
    }

    public static Uint8 valueOf(byte byteVal) {
        return instanceFor(byteVal);
    }

    public static Uint8 valueOf(short shortVal) {
        Prediction.checkRange(shortVal, MAX_VALUE);
        return instanceFor((byte) (shortVal & 0xff));
    }

    public static Uint8 valueOf(int intVal) {
        Prediction.checkRange(intVal, MAX_VALUE);
        return instanceFor((byte) (intVal & 0xff));
    }

    public static Uint8 valueOf(long longVal) {
        Prediction.checkRange(longVal, MAX_VALUE);
        return instanceFor((byte) (longVal & 0xff));
    }

    public static Uint8 valueOf(Uint16 uint) {
        return valueOf(uint.intValue());
    }

    public static Uint8 valueOf(Uint32 uint) {
        return valueOf(uint.longValue());
    }

    public static Uint8 valueOf(Uint64 uint) {
        return valueOf(uint.longValue());
    }

    public static Uint8 valueOf(String string) {
        return valueOf(string, 10);
    }

    public static Uint8 valueOf(String string, int radix) {
        return valueOf(Short.parseShort(string, radix));
    }

    @Override
    public final byte byteValue() {
        return value;
    }

    @Override
    public final int intValue() {
        return Byte.toUnsignedInt(value);
    }

    @Override
    public final long longValue() {
        return Byte.toUnsignedLong(value);
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
    public final int compareTo(Uint8 o) {
        return intValue() - o.intValue();
    }

    @Override
    public final int hashCode() {
        return Byte.hashCode(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this == obj || obj instanceof Uint8 && value == ((Uint8) obj).value;
    }

    @Override
    public final String toString() {
        return Integer.toString(intValue(), 16);
    }
}
