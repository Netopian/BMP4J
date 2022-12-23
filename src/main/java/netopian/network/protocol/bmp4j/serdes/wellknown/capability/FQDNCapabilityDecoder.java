package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint8;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FQDNCapabilityDecoder implements CapabilityDecoder {

    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // draft-walton-bgp-hostname-capability
    private static final int CAP_CODE = 73;

    @Override
    public Capability.FQDNCapability translate(ByteBuf buffer) {
        Uint8 hostNameLen = Sequential.readUint8(buffer);
        if (buffer.readableBytes() <= hostNameLen.intValue()) {
            log.warn("FQDN Capability hostNameLen is: {}, but {} bytes remain.", hostNameLen.intValue(),
                buffer.readableBytes());
            buffer.release();
            return null;
        }

        String hostName = Sequential.read2UTF8(buffer, hostNameLen.intValue());
        Uint8 domainNameLen = Sequential.readUint8(buffer);
        if (buffer.readableBytes() < domainNameLen.intValue()) {
            log.warn("FQDN Capability domainNameLen is: {}, but {} bytes remain.", hostNameLen.intValue(),
                buffer.readableBytes());
            buffer.release();
            return null;
        }
        String domainName = Sequential.read2UTF8(buffer, domainNameLen.intValue());

        return Capability.FQDNCapability.newBuilder()
            .setHostNameLen(hostNameLen.intValue())
            .setHostName(hostName)
            .setDomainNameLen(domainNameLen.intValue())
            .setDomainName(domainName)
            .build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
