package netopian.network.protocol.bmp4j.serdes;

import java.util.Objects;
import java.util.function.Function;

import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint32;
import netopian.network.protocol.bmp4j.utils.Uint8;

import io.netty.buffer.ByteBuf;

public final class BmpCommonHeader {

    private Uint8 version;

    private Uint32 length;

    private Uint8 type;

    public ByteBuf readVersion(ByteBuf byteBuf) {
        version = Sequential.readUint8(byteBuf);
        return byteBuf;
    }

    public ByteBuf readType(ByteBuf byteBuf) {
        type = Sequential.readUint8(byteBuf);
        return byteBuf;
    }

    public ByteBuf readLength(ByteBuf byteBuf) {
        length = Sequential.readUint32(byteBuf);
        return byteBuf;
    }

    public Uint8 version() {
        return Objects.requireNonNull(version, "version filed in bmp common header is not initialized.");
    }

    public Uint32 length() {
        return Objects.requireNonNull(length, "length filed in bmp common header is not initialized.");
    }

    public Uint8 type() {
        return Objects.requireNonNull(type, "type filed in bmp common header is not initialized.");
    }

    public Function<ByteBuf, ByteBuf> combine() {
        return byteBuf -> readType(readLength(readVersion(byteBuf)));
    }
}
