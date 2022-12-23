package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;

import io.netty.buffer.ByteBuf;


public class EnhancedRouteRefreshCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 7313 Enhanced Route Refresh Capability for BGP-4
    private static final int CAP_CODE = 70;

    @Override
    public Capability.EnhancedRouteRefreshCapability translate(ByteBuf buffer) {

        return Capability.EnhancedRouteRefreshCapability.newBuilder().build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
