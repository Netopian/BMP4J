package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OriginAttributeDecoder implements AttributeDecoder {

    private static final int ORIGIN_IGP = 0;

    private static final int ORIGIN_EGP = 1;

    private static final int ORIGIN_INC = 2;

    @Override
    public Attribute.OriginAttribute translate(ByteBuf byteBuf) {
        if (byteBuf.isReadable()) {
            int originCode = Sequential.readUint8(byteBuf).intValue();
            switch (originCode) {
                case ORIGIN_IGP:
                case ORIGIN_EGP:
                    return Attribute.OriginAttribute.newBuilder().setOrigin(originCode).build();
                case ORIGIN_INC:
                default:
                    return Attribute.OriginAttribute.newBuilder().setOrigin(ORIGIN_INC).build();
            }
        } else {
            log.warn("drop Origin(attrFlag {}, attrType {}) caused by unreadable input", attrFlag(), attrType());
            return null;
        }
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_ORIGIN;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_TRANSITIVE;
    }
}
