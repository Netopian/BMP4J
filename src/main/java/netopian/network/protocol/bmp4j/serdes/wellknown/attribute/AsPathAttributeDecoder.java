package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AsPathAttributeDecoder implements AttributeDecoder {

    private static final int SEG_MIN_LENGTH = 2;

    private static final int ASN_LENGTH = 4;

    @Override
    public Attribute.AsPathAttribute translate(ByteBuf byteBuf) {
        Attribute.AsPathAttribute.Builder attrBuilder = Attribute.AsPathAttribute.newBuilder();

        while (byteBuf.readableBytes() >= SEG_MIN_LENGTH) {
            Attribute.AsSegment.Builder segBuilder = Attribute.AsSegment.newBuilder();
            segBuilder.setType(Sequential.readUint8(byteBuf).intValue());
            int asCount = Sequential.readUint8(byteBuf).intValue();
            for (int i = 0; i < asCount; i++) {
                if (byteBuf.readableBytes() >= ASN_LENGTH) {
                    segBuilder.addNumbers(Sequential.readUint32(byteBuf).intValue());
                } else {
                    log.warn("incomplete Attribute.AsSegment: {} remain.", asCount - i);
                    byteBuf.release();
                    break;
                }
            }
            // may do my best
            attrBuilder.addSegments(segBuilder.build());
        }

        return attrBuilder.build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_AS_PATH;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_TRANSITIVE;
    }
}
