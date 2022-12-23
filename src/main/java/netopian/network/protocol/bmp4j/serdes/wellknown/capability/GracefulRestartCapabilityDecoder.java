package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;

import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GracefulRestartCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 4724 Graceful Restart Mechanism for BGP
    private static final int CAP_CODE = 64;

    private static final int MIN_LEN = 2;

    private static final int TUPLE_LEN = 5;

    @Override
    public Capability.GracefulRestartCapability translate(ByteBuf buffer) {

        Capability.GracefulRestartCapability.Builder builder = Capability.GracefulRestartCapability.newBuilder();
        if (buffer.readableBytes() < MIN_LEN) {
            log.warn("GracefulRestart Capability length should be greater than: {}, but {} bytes remain.", MIN_LEN,
                buffer.readableBytes());
            buffer.release();
            return null;
        }

        Uint16 flagsAndTime = Sequential.readUint16(buffer);
        builder.setFlags((flagsAndTime.intValue() >>> 12) & 0xF).setTime(flagsAndTime.intValue() & 0xFFF);

        while (buffer.readableBytes() >= TUPLE_LEN) {
            builder.addTuples(Capability.GracefulRestartCapabilityTuple.newBuilder()
                .setFamily(Gobgp.Family.newBuilder()
                    .setAfiValue(Sequential.readUint16(buffer).intValue())
                    .setSafiValue(Sequential.readUint8(buffer).intValue())
                    .build())
                .setFlags(Sequential.readUint8(buffer).intValue())
                .build());
        }

        if (buffer.isReadable()) {
            log.warn("incomplete GracefulRestart Capability translation, {} bytes remain.", buffer.readableBytes());
            buffer.release();
            return null;
        }

        return builder.build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
