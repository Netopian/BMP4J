package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FourOctetASNumberCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // rfc4893.BGP Support for Four-octet AS Number Space
    private static final int CAP_CODE = 65;

    private static final int CAP_LENGTH = 4;

    @Override
    public Capability.FourOctetASNumberCapability translate(ByteBuf buffer) {

        if (buffer.readableBytes() != CAP_LENGTH) {
            log.warn("FourOctetASNumber Capability length should be: {}, but {} bytes remain.", CAP_LENGTH,
                buffer.readableBytes());
            buffer.release();
            return null;
        }

        return Capability.FourOctetASNumberCapability.newBuilder()
            .setAs(Sequential.readUint32(buffer).intValue())
            .build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
