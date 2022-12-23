package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.Collections;
import java.util.List;

import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitiationDecoder extends IdAwareDecoder {

    private static final int TUPLE_MIN_LEN = 4;

    private static final int HANDLE_MSG_TYPE = 4;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public InitiationDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    public List<BmpMessage.InitiationMessage> translate(ByteBuf byteBuf) {
        BmpMessage.InitiationMessage.Builder builder = BmpMessage.InitiationMessage.newBuilder();
        builder.setId(idSupplier.get());

        while (byteBuf.readableBytes() >= TUPLE_MIN_LEN) {
            Uint16 type = Sequential.readUint16(byteBuf);
            Uint16 length = Sequential.readUint16(byteBuf);
            if (byteBuf.readableBytes() < length.intValue()) {
                log.warn("incomplete InitiationMessage translation. decode length is: {}, but {} bytes remain.",
                    length.intValue(), byteBuf.readableBytes());
                byteBuf.release();
                return Collections.emptyList();
            }
            if (type.equals(Uint16.valueOf(1))) {
                builder.setDescription(Sequential.read2UTF8(byteBuf, length.intValue()));
            } else if (type.equals(Uint16.valueOf(2))) {
                builder.setName(Sequential.read2UTF8(byteBuf, length.intValue()));
            } else {
                builder.addInformation(Sequential.read2UTF8(byteBuf, length.intValue()));
            }
        }
        return Collections.singletonList(builder.build());
    }
}
