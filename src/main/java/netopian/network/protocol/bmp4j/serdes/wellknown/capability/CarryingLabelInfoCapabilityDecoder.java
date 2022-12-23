package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;

import io.netty.buffer.ByteBuf;


public class CarryingLabelInfoCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 3107 Carrying Label Information in BGP-4
    private static final int CAP_CODE = 4;

    @Override
    public Capability.CarryingLabelInfoCapability translate(ByteBuf buffer) {

        return Capability.CarryingLabelInfoCapability.newBuilder().build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
