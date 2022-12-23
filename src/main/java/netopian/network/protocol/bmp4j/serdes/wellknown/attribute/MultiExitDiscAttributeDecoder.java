package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MultiExitDiscAttributeDecoder implements AttributeDecoder {

    private static final int MED_BYTES_LENGTH = 4;

    @Override
    public Attribute.MultiExitDiscAttribute translate(ByteBuf byteBuf) {
        Attribute.MultiExitDiscAttribute.Builder builder = Attribute.MultiExitDiscAttribute.newBuilder();

        if (byteBuf.readableBytes() == MED_BYTES_LENGTH) {
            return builder.setMed(Sequential.readUint32(byteBuf).intValue()).build();
        } else {
            log.warn("bad MultiExitDiscAttribute bytes count: {} which should be {}.", byteBuf.readableBytes(),
                MED_BYTES_LENGTH);
            byteBuf.release();
            return null;
        }
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_MULTI_EXIT_DISC;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }
}
