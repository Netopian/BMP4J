package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;
import netopian.network.protocol.bmp4j.utils.Uint32;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NextHopAttributeDecoder implements AttributeDecoder {
    private static final int IP4_LENGTH = 4;

    private static final int IPV6_LENGTH = 16;

    @Override
    public Attribute.NextHopAttribute translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() == IPV6_LENGTH) {
            Uint16[] ips = new Uint16[8];
            for (int i = 0; i < 8; i++) {
                ips[i] = Sequential.readUint16(byteBuf);
            }
            return Attribute.NextHopAttribute.newBuilder().setNextHop(Address.Uint16sToIpv6(ips)).build();
        } else if (byteBuf.readableBytes() == IP4_LENGTH) {
            Uint32 ips = Sequential.readUint32(byteBuf);
            return Attribute.NextHopAttribute.newBuilder().setNextHop(Address.uint32ToIpv4(ips)).build();
        } else if (byteBuf.readableBytes() == IPV6_LENGTH * 2) {
            Uint16[] global = new Uint16[8];
            for (int i = 0; i < 8; i++) {
                global[i] = Sequential.readUint16(byteBuf);
            }
            Uint16[] local = new Uint16[8];
            for (int i = 0; i < 8; i++) {
                local[i] = Sequential.readUint16(byteBuf);
            }
            return Attribute.NextHopAttribute.newBuilder()
                .setNextHop(Address.Uint16sToIpv6(global) + " / " + Address.Uint16sToIpv6(local))
                .build();
        } else {
            log.warn("bad NextHopAttribute bytes count: {} which should be one of  4/16/32.", byteBuf.readableBytes());
            byteBuf.release();
            return null;
        }
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_NEXT_HOP;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_TRANSITIVE;
    }
}
