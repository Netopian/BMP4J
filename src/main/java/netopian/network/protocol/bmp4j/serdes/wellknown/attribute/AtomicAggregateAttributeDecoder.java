package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AtomicAggregateAttributeDecoder implements AttributeDecoder {

    @Override
    public Attribute.AtomicAggregateAttribute translate(ByteBuf byteBuf) {
        return Attribute.AtomicAggregateAttribute.newBuilder().build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_ATOMIC_AGGREGATE;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH;
    }
}
