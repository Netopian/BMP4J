package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import com.google.protobuf.ByteString;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;


public class UnknownAttributeDecoder implements AttributeDecoder {

    private final Integer flags;

    private final Integer type;

    public UnknownAttributeDecoder(Integer flags, Integer type) {
        this.flags = flags;
        this.type = type;
    }

    @Override
    public Attribute.UnknownAttribute translate(ByteBuf byteBuf) {
        return Attribute.UnknownAttribute.newBuilder()
            .setType(attrType())
            .setFlags(attrFlag())
            .setValue(ByteString.copyFromUtf8(Sequential.read2UTF8(byteBuf, byteBuf.readableBytes())))
            .build();
    }

    @Override
    public int attrType() {
        return type;
    }

    @Override
    public int attrFlag() {
        return flags;
    }
}
