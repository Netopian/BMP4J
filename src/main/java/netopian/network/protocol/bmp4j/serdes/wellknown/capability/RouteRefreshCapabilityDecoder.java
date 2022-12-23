package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;

import io.netty.buffer.ByteBuf;


public class RouteRefreshCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 2918 Route Refresh Capability for BGP-4
    private static final int CAP_CODE = 2;

    @Override
    public Capability.RouteRefreshCapability translate(ByteBuf buffer) {
        return Capability.RouteRefreshCapability.newBuilder().build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
