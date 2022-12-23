package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import netopian.network.protocol.bmp4j.serdes.concept.BgpPduDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RouteMonitoringDecoder extends PerPeerDecoder {
    private static final int HANDLE_MSG_TYPE = 0;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public RouteMonitoringDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    protected List<BmpMessage.RouteMonitoringMessage> subTranslate(ByteBuf byteBuf) {

        BmpMessage.BgpUpdate updateMessage =
            BgpPduDecoder.Helper.getDecoderWithKey(BgpPduDecoder.TYPE_UPDATE).translate(byteBuf);

        if (updateMessage == null) {
            return Collections.emptyList();
        }

        if (updateMessage.getWithdrawnRoutesCount() != 0) {
            return updateMessage.getWithdrawnRoutesList()
                .stream()
                .map((withdrawn) -> BmpMessage.RouteMonitoringMessage.newBuilder()
                    .setNlri(withdrawn)
                    .setFamily(updateMessage.getFamily())
                    .setIsWithdraw(true)
                    .addAllPattrs(updateMessage.getPattrsList())
                    .setPeerHeader(headerSupplier.get())
                    .setId(idSupplier.get())
                    .build())
                .collect(Collectors.toList());
        }

        if (updateMessage.getNlriCount() != 0) {
            return updateMessage.getNlriList()
                .stream()
                .map((nlri) -> BmpMessage.RouteMonitoringMessage.newBuilder()
                    .setNlri(nlri)
                    .setFamily(updateMessage.getFamily())
                    .setIsWithdraw(false)
                    .addAllPattrs(updateMessage.getPattrsList())
                    .setPeerHeader(headerSupplier.get())
                    .setId(idSupplier.get())
                    .build())
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
