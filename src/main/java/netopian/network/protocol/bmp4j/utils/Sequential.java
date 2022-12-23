package netopian.network.protocol.bmp4j.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.netty.buffer.ByteBuf;


public final class Sequential {
    private Sequential() {
        // in purpose
    }

    public static Uint8 readUint8(ByteBuf buf) {
        return Uint8.fromByteBits(buf.readByte());
    }

    public static Uint16 readUint16(ByteBuf buf) {
        return Uint16.fromShortBits(buf.readShort());
    }

    public static Uint32 readUint32(ByteBuf buf) {
        return Uint32.fromIntBits(buf.readInt());
    }

    public static Uint64 readUint64(ByteBuf buf) {
        return Uint64.fromLongBits(buf.readLong());
    }

    public static String read2UTF8(ByteBuf buf, int length) {
        return buf.readBytes(length).toString(StandardCharsets.UTF_8);
    }

    public static void read2bytes(ByteBuf buf, byte[] dst) {
        buf.readBytes(Objects.requireNonNull(dst));
    }

    public static ByteBuf read2ByteBuf(ByteBuf buf, int length) {
        return buf.readBytes(length);
    }

    public static void read2buffer(ByteBuf buf, ByteBuffer dst) {
        buf.readBytes(Objects.requireNonNull(dst));
    }

    public static ByteBuf drop(ByteBuf byteBuf, int byteCount) {
        for (int i = 0; i < byteCount; i++) {
            Sequential.readUint8(byteBuf);
        }
        return byteBuf;
    }

    private static void writeUint8(ByteBuf buf, Uint8 value) {
        buf.writeByte(value.byteValue());
    }

    private static void writeUint16(ByteBuf buf, Uint16 value) {
        buf.writeShort(value.shortValue());
    }

    private static void writeUint32(ByteBuf buf, Uint32 value) {
        buf.writeInt(value.intValue());
    }

    private static void writeUint64(ByteBuf buf, Uint64 value) {
        buf.writeLong(value.longValue());
    }

    public static void write(ByteBuf buf, Uint8 value) {
        writeUint8(buf, value);
    }

    public static void write(ByteBuf buf, Uint16 value) {
        writeUint16(buf, value);
    }

    public static void write(ByteBuf buf, Uint32 value) {
        writeUint32(buf, value);
    }

    public static void write(ByteBuf buf, Uint64 value) {
        writeUint64(buf, value);
    }

    public static void writeOptional(ByteBuf buf, Byte value) {
        if (value != null) {
            buf.writeByte(value);
        }
    }

    public static void writeOptional(ByteBuf buf, Short value) {
        if (value != null) {
            buf.writeShort(value);
        }
    }

    public static void writeOptional(ByteBuf buf, Integer value) {
        if (value != null) {
            buf.writeInt(value);
        }
    }

    public static void writeOptional(ByteBuf buf, Long value) {
        if (value != null) {
            buf.writeLong(value);
        }
    }

    public static void writeOptional(ByteBuf buf, Uint8 value) {
        if (value != null) {
            write(buf, value);
        }
    }

    public static void writeOptional(ByteBuf buf, Uint16 value) {
        if (value != null) {
            write(buf, value);
        }
    }

    public static void writeOptional(ByteBuf buf, Uint32 value) {
        if (value != null) {
            write(buf, value);
        }
    }

    public static void writeOptional(ByteBuf buf, Uint64 value) {
        if (value != null) {
            write(buf, value);
        }
    }

    public static void writeOrZero(ByteBuf buf, Byte value) {
        buf.writeByte(value != null ? value : 0);
    }

    public static void writeOrZero(ByteBuf buf, Short value) {
        buf.writeShort(value != null ? value : 0);
    }

    public static void writeOrZero(ByteBuf buf, Integer value) {
        buf.writeInt(value != null ? value : 0);
    }

    public static void writeOrZero(ByteBuf buf, Long value) {
        buf.writeLong(value != null ? value : 0L);
    }

    public static void writeOrZero(ByteBuf buf, Uint8 value) {
        buf.writeByte(value != null ? value.byteValue() : 0);
    }

    public static void writeOrZero(ByteBuf buf, Uint16 value) {
        buf.writeShort(value != null ? value.shortValue() : 0);
    }

    public static void writeOrZero(ByteBuf buf, Uint32 value) {
        buf.writeInt(value != null ? value.intValue() : 0);
    }

    public static void writeOrZero(ByteBuf buf, Uint64 value) {
        buf.writeLong(value != null ? value.longValue() : 0L);
    }
}
