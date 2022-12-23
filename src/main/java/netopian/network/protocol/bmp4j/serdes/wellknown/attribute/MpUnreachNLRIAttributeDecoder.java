package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MpUnreachNLRIAttributeDecoder implements AttributeDecoder {

    /*
     * +---------------------------------------------------------+
     * | Address Family Identifier (2 octets)------------------- |
     * +---------------------------------------------------------+
     * | Subsequent Address Family Identifier (1 octet)--------- |
     * +---------------------------------------------------------+
     * | Withdrawn Routes (variable)---------------------------- |
     * +---------------------------------------------------------+
     */

    private static final int MIN_LENGTH_LIMIT = 3;

    @Override
    public int attrType() {
        return ATTR_TYPE_MP_UNREACH_NLRI;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }

    @Override
    public Attribute.MpUnreachNLRIAttribute translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < MIN_LENGTH_LIMIT) {
            log.warn("bad MpUnreachNLRIAttribute bytes count: {} which should be {}.", byteBuf.readableBytes(),
                MIN_LENGTH_LIMIT);
            byteBuf.release();
            return null;
        }

        int afi = Sequential.readUint16(byteBuf).intValue();
        int safi = Sequential.readUint8(byteBuf).intValue();
        Attribute.MpUnreachNLRIAttribute.Builder builder = Attribute.MpUnreachNLRIAttribute.newBuilder();

        if (byteBuf.isReadable()) {
            NlriDecoder.Helper.getDecoderWithKey(afi & 0xffffffff + safi & 0xff + NlriDecoder.NOT_SET)
                .translate(byteBuf)
                .forEach((nlri) -> builder.addNlris(Any.pack((GeneratedMessageV3) nlri)));
        }

        return builder.build();
    }
}
