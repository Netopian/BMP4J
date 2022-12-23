package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;

import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteMirroringDecoder extends PerPeerDecoder {
    private static final int HANDLE_MSG_TYPE = 6;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public RouteMirroringDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    protected List<BmpMessage.RouteMirroringMessage> subTranslate(ByteBuf byteBuf) {
        List<BmpMessage.RouteMirroringMessage> result = new ArrayList<>();

        while (byteBuf.readableBytes() >= 4) {
            BmpMessage.RouteMirroringMessage.Builder builder = BmpMessage.RouteMirroringMessage.newBuilder();
            Uint16 length = Sequential.readUint16(Sequential.drop(byteBuf, 2));
            if (byteBuf.readableBytes() < length.intValue()) {
                log.warn("incomplete RouteMirroringMessage translation. decode length is {}, {} bytes remain.",
                    length.intValue(), byteBuf.readableBytes());
                byteBuf.release();
            }
            byte[] meta = new byte[length.intValue()];
            Sequential.read2bytes(byteBuf, meta);
            builder.addErrorsMeta(ByteString.copyFrom(meta));
            builder.setId(idSupplier.get()).setPeerHeader(headerSupplier.get());
            result.add(builder.build());
        }

        if (byteBuf.isReadable()) {
            log.warn("incomplete RouteMirroringMessage translation, {} bytes remain.", byteBuf.readableBytes());
            byteBuf.release();
        }

        return result;
    }

}
