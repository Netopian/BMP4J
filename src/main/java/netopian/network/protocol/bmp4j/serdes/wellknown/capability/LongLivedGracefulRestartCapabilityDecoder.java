package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LongLivedGracefulRestartCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // draft-uttaro-idr-bgp-persistence-05
    private static final int CAP_CODE = 71;

    private static final int TUPLE_LEN = 7;

    @Override
    public Capability.LongLivedGracefulRestartCapability translate(ByteBuf buffer) {

        Capability.LongLivedGracefulRestartCapability.Builder builder =
            Capability.LongLivedGracefulRestartCapability.newBuilder();

        while (buffer.readableBytes() >= TUPLE_LEN) {
            builder.addTuples(Capability.LongLivedGracefulRestartCapabilityTuple.newBuilder()
                .setFamily(Gobgp.Family.newBuilder()
                    .setAfiValue(Sequential.readUint16(buffer).intValue())
                    .setSafiValue(Sequential.readUint8(buffer).intValue())
                    .build())
                .setFlags(Sequential.readUint8(buffer).intValue())
                .setTime(Sequential.readUint16(buffer).intValue() << 8 + Sequential.readUint8(buffer).intValue())
                .build());
        }

        if (buffer.isReadable()) {
            log.warn("incomplete LongLived GracefulRestart Capability translation, {} bytes remain.",
                buffer.readableBytes());
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
