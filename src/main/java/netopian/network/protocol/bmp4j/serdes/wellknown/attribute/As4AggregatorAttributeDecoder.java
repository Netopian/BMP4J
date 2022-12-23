package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class As4AggregatorAttributeDecoder implements AttributeDecoder {

    @Override
    public Attribute.As4AggregatorAttribute translate(ByteBuf byteBuf) {
        log.info("Caution: AS4 Aggregator(attrFlag {}, attrType {}) is ignored", attrFlag(), attrType());
        byteBuf.release();
        return null;
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_AS4_AGGREGATOR;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
