package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MultiPProtocolCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 2858 Multiprotocol Extensions for BGP-4
    private static final int CAP_CODE = 1;

    private static final int CAP_LENGTH = 4;

    @Override
    public Capability.MultiProtocolCapability translate(ByteBuf buffer) {
        if (buffer.readableBytes() != CAP_LENGTH) {
            log.warn("MultiProtocol Capability length should be: {}, but {} bytes remain.", CAP_LENGTH,
                buffer.readableBytes());
            buffer.release();
            return null;
        }

        return Capability.MultiProtocolCapability.newBuilder()
            .setFamily(Gobgp.Family.newBuilder()
                .setAfiValue(Sequential.readUint16(buffer).intValue())
                .setSafiValue(Sequential.readUint16(buffer).intValue() & 0xff)
                .build())
            .build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
