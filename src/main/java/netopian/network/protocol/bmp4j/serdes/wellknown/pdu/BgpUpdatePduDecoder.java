package netopian.network.protocol.bmp4j.serdes.wellknown.pdu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.serdes.concept.BgpPduDecoder;
import netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BgpUpdatePduDecoder implements BgpPduDecoder {

    private static final int ATTR_MIN_LEN = 3;

    private static final int IP4_UNICAST_NLRI = (1 << 16) + (1 << 8);

    @Override
    public int pduType() {
        return BgpPduDecoder.TYPE_UPDATE;
    }

    @Override
    public BmpMessage.BgpUpdate translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < MIN_LENGTH) {
            log.warn("bad RouteMonitoringMessage bytes count: {} which should be more than {}.",
                byteBuf.readableBytes(), MIN_LENGTH);
            byteBuf.release();
            return null;
        }
        // ignore marker in purpose.
        int pduLen = Sequential.readUint16(Sequential.drop(byteBuf, MARKER_LEN)).intValue() - MIN_LENGTH;
        int pduType = Sequential.readUint8(byteBuf).intValue();
        if (pduType != BgpPduDecoder.TYPE_UPDATE || pduLen <= 0 || byteBuf.readableBytes() < pduLen) {
            log.warn("incomplete update pdu with length {}, {} bytes remain", pduLen, byteBuf.readableBytes());
            byteBuf.release();
            return null;
        }

        BmpMessage.BgpUpdate.Builder builder = BmpMessage.BgpUpdate.newBuilder()
            .setFamily(
                BmpMessage.Family.newBuilder().setAfi(NlriDecoder.AFI_IP).setSafi(NlriDecoder.SAFI_UNICAST).build());

        // withdrawn section.
        int withdrawnLength = Sequential.readUint16(byteBuf).intValue();
        if (withdrawnLength > 0 && byteBuf.readableBytes() >= withdrawnLength) {
			ByteBuf buf = Sequential.read2ByteBuf(byteBuf, withdrawnLength);
            Collection<Attribute.IPAddressPrefix> withdrawn = NlriDecoder.Helper.getDecoderWithKey(IP4_UNICAST_NLRI)
                .translate(buf);
			buf.release();
            if (byteBuf.isReadable()) {
                log.warn("withdrawn message should be isolated but {} bytes remain.", byteBuf.readableBytes());
                byteBuf.release();
            }
            return builder.addAllWithdrawnRoutes(withdrawn.parallelStream().map(Any::pack).collect(Collectors.toList()))
                .build();
        }

        // attribute section.
        int attrLength = Sequential.readUint16(byteBuf).intValue();
        if (attrLength >= 0 && byteBuf.readableBytes() >= attrLength) {
            ByteBuf buf = Sequential.read2ByteBuf(byteBuf, attrLength);
            builder.addAllPattrs(decodeAttrs(buf, builder));
            buf.release();
        } else {
            log.warn("attr length is {}, but {} bytes remain.", attrLength, byteBuf.readableBytes());
            byteBuf.release();
        }

        // NLRI section.
        if (byteBuf.isReadable()) {
            builder.addAllNlri(NlriDecoder.Helper.getDecoderWithKey(IP4_UNICAST_NLRI)
                .translate(byteBuf)
                .parallelStream()
                .map(Any::pack)
                .collect(Collectors.toList()));
        }
        return builder.build();
    }

    private Collection<Any> decodeAttrs(ByteBuf byteBuf, BmpMessage.BgpUpdate.Builder extBuilder) {
        Collection<Any> attrs = new ArrayList<>();

        while (byteBuf.readableBytes() >= ATTR_MIN_LEN) {
            int flag = Sequential.readUint8(byteBuf).intValue();
            int type = Sequential.readUint8(byteBuf).intValue();
            int length = Sequential.readUint8(byteBuf).intValue();
            if (byteBuf.readableBytes() < length) {
                log.warn("incomplete attr translation with length {}, {} bytes remain.", length,
                    byteBuf.readableBytes());
                byteBuf.release();
                break;
            }
			ByteBuf buf = Sequential.read2ByteBuf(byteBuf, length);
            GeneratedMessageV3 attr = AttributeDecoder.Helper.getDecoderWithType(flag, type)
                .translate(buf);
			buf.release();
			
            if (type == AttributeDecoder.ATTR_TYPE_MP_REACH_NLRI) {
                Attribute.MpReachNLRIAttribute temp = (Attribute.MpReachNLRIAttribute) attr;
                extBuilder.addAllNlri(temp.getNlrisList());
                extBuilder.setFamily(BmpMessage.Family.newBuilder()
                    .setAfi(temp.getFamily().getAfiValue())
                    .setSafi(temp.getFamily().getSafiValue())
                    .build());
                attrs.add(Any
                    .pack(Attribute.MpReachNLRIAttribute.newBuilder().addAllNextHops(temp.getNextHopsList()).build()));
            } else if (type == AttributeDecoder.ATTR_TYPE_MP_UNREACH_NLRI) {
                Attribute.MpUnreachNLRIAttribute temp = (Attribute.MpUnreachNLRIAttribute) attr;
                extBuilder.addAllWithdrawnRoutes(temp.getNlrisList());
                extBuilder.setFamily(BmpMessage.Family.newBuilder()
                    .setAfi(temp.getFamily().getAfiValue())
                    .setSafi(temp.getFamily().getSafiValue())
                    .build());
            } else {
                attrs.add(Any.pack(attr));
            }
        }
        return attrs;
    }
}
