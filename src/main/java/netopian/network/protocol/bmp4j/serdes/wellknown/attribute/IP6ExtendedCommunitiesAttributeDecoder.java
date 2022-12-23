package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IP6ExtendedCommunitiesAttributeDecoder implements AttributeDecoder {

    // IPv6 Address Specific BGP Extended Community Attribute
    @Override
    public Attribute.IP6ExtendedCommunitiesAttribute translate(ByteBuf byteBuf) {
        log.info("Caution: IP6 Extended Communities(attrFlag {}, attrType {}) is ignored", attrFlag(), attrType());
        byteBuf.release();
        return null;
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_IP6_EXTENDED_COMMUNITIES;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
