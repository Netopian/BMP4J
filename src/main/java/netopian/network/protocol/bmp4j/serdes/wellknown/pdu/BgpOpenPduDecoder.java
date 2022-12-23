package netopian.network.protocol.bmp4j.serdes.wellknown.pdu;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Any;

import netopian.network.protocol.bmp4j.serdes.concept.BgpPduDecoder;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint8;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BgpOpenPduDecoder implements BgpPduDecoder {

    private static final int CAP_PARA_TYPE = 2;

    @Override
    public int pduType() {
        return BgpPduDecoder.TYPE_OPEN;
    }

    @Override
    public BmpMessage.BgpOpen translate(ByteBuf byteBuf) {

        if (byteBuf.readableBytes() < MIN_LENGTH) {
            log.warn("readable buf size {} is less than open message min-length: {}", byteBuf.readableBytes(),
                MIN_LENGTH);
            byteBuf.release();
            return null;
        }
        int pduLen = Sequential.readUint16(Sequential.drop(byteBuf, MARKER_LEN)).intValue() - MIN_LENGTH;
        Uint8 type = Sequential.readUint8(byteBuf);
        if (type.intValue() != BgpPduDecoder.TYPE_OPEN || pduLen < 0 || byteBuf.readableBytes() < pduLen) {
            log.warn("incomplete open pdu with length {}, {} bytes remain", pduLen, byteBuf.readableBytes());
            byteBuf.release();
            return null;
        }

        return decodePdu(Sequential.read2ByteBuf(byteBuf, pduLen));
    }

    private BmpMessage.BgpOpen decodePdu(ByteBuf pduBuf) {
        // 10 = version(1) + ASN(2) + HoldTime(2) + BGPID(4) + OPTLENGTH(1)
        if (pduBuf.readableBytes() < 10) {
            return null;
        }

        BmpMessage.BgpOpen.Builder builder = BmpMessage.BgpOpen.newBuilder();
        builder.setVersion(Sequential.readUint8(pduBuf).intValue())
            .setMyAsNumber(Sequential.readUint16(pduBuf).intValue())
            .setHoldTimer(Sequential.readUint16(pduBuf).intValue())
            .setBgpIdentifier(Address.uint32ToIpv4(Sequential.readUint32(pduBuf)));

        int optLen = Sequential.readUint8(pduBuf).intValue();
        if (optLen != 0 && pduBuf.readableBytes() >= optLen) {
            builder.addAllCapabilities(decodeCapabilities(pduBuf));
        }

        if (pduBuf.isReadable()) {
            pduBuf.release();
        }
        return builder.build();
    }

    private List<Any> decodeCapabilities(ByteBuf byteBuf) {
        List<Any> result = new ArrayList<>();

        while (byteBuf.readableBytes() >= 2) {
            int paraType = Sequential.readUint8(byteBuf).intValue();
            int paraLen = Sequential.readUint8(byteBuf).intValue();
            if (paraType == CAP_PARA_TYPE && (paraLen > 0 && byteBuf.readableBytes() >= paraLen)) {
                ByteBuf capBuf = Sequential.read2ByteBuf(byteBuf, paraLen);
                while (capBuf.readableBytes() >= 2) {
                    result.add(
                        Any.pack(CapabilityDecoder.Helper.getDecoderWithKey(Sequential.readUint8(capBuf).intValue())
                            .translate(Sequential.read2ByteBuf(capBuf, Sequential.readUint8(capBuf).intValue()))));
                }
            } else {
                log.warn("incomplete capabilities translation with length {}, {} bytes remain", paraLen,
                    byteBuf.readableBytes());
                break;
            }
        }
        return result;
    }
}
