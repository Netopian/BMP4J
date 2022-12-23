package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddPathCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 7911 Advertisement of Multiple Paths in BGP
    private static final int CAP_CODE = 69;

    private static final int TUPLE_LENGTH = 4;

    @Override
    public Capability.AddPathCapability translate(ByteBuf buffer) {

        Capability.AddPathCapability.Builder builder = Capability.AddPathCapability.newBuilder();

        while (buffer.readableBytes() >= TUPLE_LENGTH) {
            builder.addTuples(Capability.AddPathCapabilityTuple.newBuilder()
                .setFamily(Gobgp.Family.newBuilder()
                    .setAfiValue(Sequential.readUint16(buffer).intValue())
                    .setSafiValue(Sequential.readUint8(buffer).intValue())
                    .build())
                .setModeValue(Sequential.readUint8(buffer).intValue())
                .build());
        }

        if (buffer.isReadable()) {
            log.warn("incomplete AddPath Capability translation, {} bytes remain.", buffer.readableBytes());
            buffer.release();
        }

        return builder.build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
