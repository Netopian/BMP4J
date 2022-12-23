package netopian.network.protocol.bmp4j.utils;


class Prediction {

    private Prediction() {
        // in purpose
    }

    static void checkRange(byte value, byte max) {
        if (value < 0 || value > max) {
            throwIAE(value, max);
        }

    }

    static void checkRange(short value, short max) {
        if (value < 0 || value > max) {
            throwIAE(value, max);
        }

    }

    static void checkRange(int value, int max) {
        if (value < 0 || value > max) {
            throwIAE(value, max);
        }

    }

    static void checkRange(long value, long max) {
        if (value < 0L || value > max) {
            throwIAE(value, max);
        }

    }

    static void checkRange(long value, String max) {
        if (value < 0) {
            throwIAE(value, max);
        }

    }

    static void throwIAE(short value, short max) {
        throw new IllegalArgumentException("Invalid range: " + value + ", expected: [[0.." + max + "]].");
    }

    static void throwIAE(int value, int max) {
        throw new IllegalArgumentException("Invalid range: " + value + ", expected: [[0.." + max + "]].");
    }

    static void throwIAE(long value, long max) {
        throw new IllegalArgumentException("Invalid range: " + value + ", expected: [[0.." + max + "]].");
    }

    static void throwIAE(long value, String max) {
        throw new IllegalArgumentException("Invalid range: " + value + ", expected: [[0.." + max + "]].");
    }
}
