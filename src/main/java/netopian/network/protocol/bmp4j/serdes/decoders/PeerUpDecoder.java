package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import netopian.network.protocol.bmp4j.serdes.concept.BgpPduDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PeerUpDecoder extends PerPeerDecoder {

    private static final int IP_BYTES_LENGTH = 16;

    private static final int IDENTIFY_LENGTH = 20;

    private static final int HANDLE_MSG_TYPE = 3;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public PeerUpDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    protected List<BmpMessage.PeerUpNotification> subTranslate(ByteBuf byteBuf) {

        if (byteBuf.readableBytes() < IDENTIFY_LENGTH) {
            log.warn("can not translate PeerUpNotification, {} is less than min-length {}.", byteBuf.readableBytes(),
                IDENTIFY_LENGTH);
            byteBuf.release();
            return Collections.emptyList();
        }

        return Collections.singletonList(BmpMessage.PeerUpNotification.newBuilder()
            .setLocalAddress(isIPv4Supplier.get() ? decodeIP4(Sequential.read2ByteBuf(byteBuf, IP_BYTES_LENGTH))
                : decodeIP6(Sequential.read2ByteBuf(byteBuf, IP_BYTES_LENGTH)))
            .setLocalPort(Sequential.readUint16(byteBuf).intValue())
            .setRemotePort(Sequential.readUint16(byteBuf).intValue())
            .setOpenSent(BgpPduDecoder.Helper.getDecoderWithKey(BgpPduDecoder.TYPE_OPEN).translate(byteBuf))
            .setOpenReceived(BgpPduDecoder.Helper.getDecoderWithKey(BgpPduDecoder.TYPE_OPEN).translate(byteBuf))
            .addAllInformation(decodeInformation(byteBuf))
            .setId(idSupplier.get())
            .setPeerHeader(headerSupplier.get())
            .build());
    }

    private String decodeIP6(ByteBuf byteBuf) {
        Uint16[] temp = new Uint16[8];
        for (int i = 0; i < 8; i++) {
            temp[i] = Sequential.readUint16(byteBuf);
        }
        return Address.Uint16sToIpv6(temp);
    }

    private String decodeIP4(ByteBuf byteBuf) {
        String ip4 = Address.uint32ToIpv4(Sequential.readUint32(Sequential.drop(byteBuf, 12)));
        byteBuf.release();
        return ip4;
    }

    private List<String> decodeInformation(ByteBuf byteBuf) {
        List<String> result = new ArrayList<>();
        while (byteBuf.readableBytes() >= 4) {
            // ignore tlv type. assume it as absolutely string.
            Sequential.drop(byteBuf, 2);
            Uint16 length = Sequential.readUint16(byteBuf);
            if (byteBuf.readableBytes() < length.intValue()) {
                log.warn("incomplete info translation. decode length is: {}, but {} bytes remain.", length.intValue(),
                    byteBuf.readableBytes());
                break;
            }
            result.add(Sequential.read2UTF8(byteBuf, length.intValue()));
        }
		byteBuf.release();
        return result;
    }
}
