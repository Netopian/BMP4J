package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CommunitiesAttributeDecoder implements AttributeDecoder {

    private static final int COMMUNITY_LENGTH = 4;

    @Override
    public Attribute.CommunitiesAttribute translate(ByteBuf byteBuf) {
        Attribute.CommunitiesAttribute.Builder builder = Attribute.CommunitiesAttribute.newBuilder();

        while (byteBuf.readableBytes() >= COMMUNITY_LENGTH) {
            builder.addCommunities(Sequential.readUint32(byteBuf).intValue());
        }

        if (byteBuf.isReadable()) {
            log.warn("incomplete Communities id, {} drops.", byteBuf.readableBytes());
            byteBuf.release();
            // do my best
        }

        return builder.build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_COMMUNITIES;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_EXTENDED_LENGTH | ATTR_FLAG_OPTIONAL;
    }
}
