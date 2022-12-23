package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OriginatorIdAttributeDecoder implements AttributeDecoder {

    private static final int IP4_LENGTH = 4;

    @Override
    public Attribute.OriginatorIdAttribute translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() != IP4_LENGTH) {
            log.warn("bad OriginatorIdAttribute bytes count: {} which should be {}.", byteBuf.readableBytes(),
                IP4_LENGTH);
            byteBuf.release();
            return null;
        }

        return Attribute.OriginatorIdAttribute.newBuilder()
            .setId(Address.uint32ToIpv4(Sequential.readUint32(byteBuf)))
            .build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_ORIGINATOR_ID;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }
}
