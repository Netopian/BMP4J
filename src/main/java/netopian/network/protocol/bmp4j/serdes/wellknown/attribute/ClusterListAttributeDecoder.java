package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClusterListAttributeDecoder implements AttributeDecoder {

    private static final int IPv4_LENGTH = 4;

    @Override
    public Attribute.ClusterListAttribute translate(ByteBuf byteBuf) {

        Attribute.ClusterListAttribute.Builder builder = Attribute.ClusterListAttribute.newBuilder();

        while (byteBuf.readableBytes() >= IPv4_LENGTH) {
            builder.addIds(Address.uint32ToIpv4(Sequential.readUint32(byteBuf)));
        }

        if (byteBuf.isReadable()) {
            log.warn("incomplete cluster id list, {} drops.", byteBuf.readableBytes());
            byteBuf.release();
            // do my best
        }

        return builder.build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_CLUSTER_LIST;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }
}
