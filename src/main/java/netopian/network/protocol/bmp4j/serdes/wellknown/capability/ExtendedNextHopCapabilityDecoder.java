package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExtendedNextHopCapabilityDecoder implements CapabilityDecoder {
    // Known capability codes: https://www.iana.org/assignments/capability-codes/capability-codes.xhtml
    // Capability structure: https://tools.ietf.org/html/rfc5492#section-4
    // RFC 8950 Advertising IPv4 Network Layer Reachability Information (NLRI) with an IPv6 Next Hop
    private static final int CAP_CODE = 5;

    private static final int TUPLE_LENGTH = 5;

    @Override
    public Capability.ExtendedNexthopCapability translate(ByteBuf buffer) {

        Capability.ExtendedNexthopCapability.Builder builder = Capability.ExtendedNexthopCapability.newBuilder();

        while (buffer.readableBytes() >= TUPLE_LENGTH) {
            builder.addTuples(Capability.ExtendedNexthopCapabilityTuple.newBuilder()
                .setNlriFamily(Gobgp.Family.newBuilder()
                    .setAfiValue(Sequential.readUint16(buffer).intValue())
                    .setSafiValue(Sequential.readUint8(buffer).intValue())
                    .build())
                .setNexthopFamily(
                    Gobgp.Family.newBuilder().setAfiValue(Sequential.readUint16(buffer).intValue()).build())
                .build());
        }

        if (buffer.isReadable()) {
            log.warn("incomplete Extended Nexthop Capability translation, {} bytes remain.", buffer.readableBytes());
            buffer.release();
        }

        return builder.build();
    }

    @Override
    public int capabilityCode() {
        return CAP_CODE;
    }
}
