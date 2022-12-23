package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AggregatorAttributeDecoder implements AttributeDecoder {
    private static final int LENGTH_LIMIT = 8;

    @Override
    public Attribute.AggregatorAttribute translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() != LENGTH_LIMIT) {
            log.warn("Aggregator Attribute length should be {}, {} bytes remain.", LENGTH_LIMIT,
                byteBuf.readableBytes());
            byteBuf.release();
            return null;
        }

        return Attribute.AggregatorAttribute.newBuilder()
            .setAs(Sequential.readUint32(byteBuf).intValue())
            .setAddress(Address.uint32ToIpv4(Sequential.readUint32(byteBuf)))
            .build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_AGGREGATOR;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
