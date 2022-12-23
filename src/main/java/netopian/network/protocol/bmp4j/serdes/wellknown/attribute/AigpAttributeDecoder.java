package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AigpAttributeDecoder implements AttributeDecoder {
    private static final int AIGP_TLV_TYPE = 1;

    private static final int TLV_BYTES_LENGTH = 8;

    @Override
    public Attribute.AigpAttribute translate(ByteBuf byteBuf) {
        Attribute.AigpAttribute.Builder builder = Attribute.AigpAttribute.newBuilder();

        while (byteBuf.isReadable()) {
            int tlvType = Sequential.readUint8(byteBuf).intValue();
            int length = Sequential.readUint16(byteBuf).intValue();
            if (byteBuf.readableBytes() < length) {
                log.warn("incomplete Aigp tlv translation, get length {}, but {} bytes remains.", length,
                    byteBuf.readableBytes());
                byteBuf.release();
                break;
            }

            if (tlvType == AIGP_TLV_TYPE) {
                if (length != TLV_BYTES_LENGTH) {
                    log.warn("get Aigp tlv length {}, which should be {}.", length, TLV_BYTES_LENGTH);
                    byteBuf.release();
                    break;
                }
                builder.addTlvs(Any.pack(Attribute.AigpTLVIGPMetric.newBuilder()
                    .setMetric(Sequential.readUint64(byteBuf).longValue())
                    .build()));
            } else {
                byte[] valueBytes = new byte[length];
                Sequential.read2bytes(byteBuf, valueBytes);
                builder.addTlvs(Any.pack(Attribute.AigpTLVUnknown.newBuilder()
                    .setType(tlvType)
                    .setValue(ByteString.copyFrom(valueBytes))
                    .build()));
            }
        }
        return builder.build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_AIGP;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }
}
