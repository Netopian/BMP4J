package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LsAttributeDecoder implements AttributeDecoder {

    @Override
    public Attribute.LsAttribute translate(ByteBuf byteBuf) {
        log.info("Caution: ls(attrFlag {}, attrType {}) is ignored", attrFlag(), attrType());
        byteBuf.release();
        return null;
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_LS;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
