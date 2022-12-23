package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExtendedCommunitiesAttributeDecoder implements AttributeDecoder {

    // rfc4360 BGP Extended Communities Attribute
    @Override
    public Attribute.ExtendedCommunitiesAttribute translate(ByteBuf byteBuf) {
        log.info("Caution: Extended Communities(attrFlag {}, attrType {}) is ignored", attrFlag(), attrType());
        byteBuf.release();
        return null;
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_EXTENDED_COMMUNITIES;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
