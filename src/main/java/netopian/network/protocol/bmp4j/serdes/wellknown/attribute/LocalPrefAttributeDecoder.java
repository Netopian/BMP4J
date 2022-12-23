package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LocalPrefAttributeDecoder implements AttributeDecoder {
    private static final int LOCPREF_BYTES_LENGTH = 4;

    @Override
    public Attribute.LocalPrefAttribute translate(ByteBuf byteBuf) {
        Attribute.LocalPrefAttribute.Builder builder = Attribute.LocalPrefAttribute.newBuilder();
        if (byteBuf.readableBytes() == LOCPREF_BYTES_LENGTH) {
            return builder.setLocalPref(Sequential.readUint32(byteBuf).intValue()).build();
        } else {
            log.warn("bad LocalPrefAttribute bytes count: {} which should be {}.", byteBuf.readableBytes(),
                LOCPREF_BYTES_LENGTH);
            byteBuf.release();
            return null;
        }
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_LOCAL_PREF;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_TRANSITIVE;
    }
}
