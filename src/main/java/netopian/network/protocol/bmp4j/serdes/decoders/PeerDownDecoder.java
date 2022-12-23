package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.Collections;
import java.util.List;

import com.google.protobuf.ByteString;

import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint8;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PeerDownDecoder extends PerPeerDecoder {
    private static final int HANDLE_MSG_TYPE = 2;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public PeerDownDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    protected List<BmpMessage.PeerDownNotification> subTranslate(ByteBuf byteBuf) {
        BmpMessage.PeerDownNotification.Builder builder =
            BmpMessage.PeerDownNotification.newBuilder().setId(idSupplier.get()).setPeerHeader(headerSupplier.get());

        Uint8 reason = Sequential.readUint8(byteBuf);
        switch (reason.intValue()) {
            case 1:
                builder.setLocalSystemClosed(true);
                if (byteBuf.readableBytes() >= 2) {
                    builder.setDataNotification(BmpMessage.DataNotification.newBuilder()
                        .setErrorCode(Sequential.readUint8(byteBuf).intValue())
                        .setErrorSubcode(Sequential.readUint8(byteBuf).intValue())
                        .setData(ByteString.copyFromUtf8(Sequential.read2UTF8(byteBuf, byteBuf.readableBytes())))
                        .build());
                }
                break;
            case 2:
                builder.setLocalSystemClosed(true);
                if (byteBuf.readableBytes() >= 2) {
                    builder.setDataFsmEventCode(BmpMessage.DataFsmEventCode.newBuilder()
                        .setFsmEventCode(Sequential.readUint16(byteBuf).intValue())
                        .build());
                }
                break;
            case 3:
                builder.setLocalSystemClosed(false);
                if (byteBuf.readableBytes() >= 2) {
                    builder.setDataNotification(BmpMessage.DataNotification.newBuilder()
                        .setErrorCode(Sequential.readUint8(byteBuf).intValue())
                        .setErrorSubcode(Sequential.readUint8(byteBuf).intValue())
                        .setData(ByteString.copyFromUtf8(Sequential.read2UTF8(byteBuf, byteBuf.readableBytes())))
                        .build());
                }
                break;
            case 4:
            case 5:
                builder.setLocalSystemClosed(false);
                break;
            default:
                log.warn("can not translate PeerUpNotification, unrecognized reason type: {}.", reason.intValue());
                byteBuf.release();
                return Collections.emptyList();
        }
        return Collections.singletonList(builder.build());
    }
}
