package netopian.network.protocol.bmp4j.serdes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import netopian.network.protocol.bmp4j.statistic.SerdesMetric;
import netopian.network.protocol.bmp4j.transmit.Transmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.serdes.decoders.IdAwareDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


@Component
public class PbDecoderSupplier implements Supplier<PbDecoderSupplier.ProtobufDecoder> {

    @Autowired
    private SerdesMetric serdesMetric;

    @Autowired
    private Transmitter transmitter;

    @Override
    public ProtobufDecoder get() {
        return new ProtobufDecoder(serdesMetric, transmitter);
    }

    public static class ProtobufDecoder extends ByteToMessageDecoder {

        /**
         * o Message Type (1 byte): This identifies the type of the BMP
         * message. A BMP implementation MUST ignore unrecognized message
         * types upon receipt.
         * * Type = 0: Route Monitoring
         * * Type = 1: Statistics Report
         * * Type = 2: Peer Down Notification
         * * Type = 3: Peer Up Notification
         * * Type = 4: Initiation Message
         * * Type = 5: Termination Message
         * * Type = 6: Route Mirroring Message
         */
        private static final String[] DECODER_NAMES = {"RouteMonitoringDecoder", "StatsReportsDecoder",
            "PeerDownDecoder", "PeerUpDecoder", "InitiationDecoder", "TerminationDecoder", "RouteMirroringDecoder"};

        private static final String PREFIX = BmpCommonHeader.class.getName().replace("BmpCommonHeader", "decoders.");

        private SerdesMetric serdesMetric;

        private Transmitter transmitter;

        public ProtobufDecoder(SerdesMetric serdesMetric, Transmitter transmitter) {
            this.serdesMetric = serdesMetric;
            this.transmitter = transmitter;
        }

        @Override
        protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list) throws Exception {
            serdesMetric.addRx(context.channel().remoteAddress(), context.channel().localAddress());

            BmpCommonHeader commonHeader = new BmpCommonHeader();

            ByteBuf payload = commonHeader.combine().apply(byteBuf);

            Class<IdAwareDecoder> decoderClazz =
                (Class<IdAwareDecoder>) Class.forName(PREFIX + DECODER_NAMES[commonHeader.type().intValue()]);

            Method translateMethod = decoderClazz.getMethod("translate", ByteBuf.class);

            Field idSupplier = decoderClazz.getField("idSupplier");

            IdAwareDecoder decoder =
                decoderClazz.getDeclaredConstructor(ChannelHandlerContext.class).newInstance(context);

            Collection<GeneratedMessageV3> result =
                (Collection<GeneratedMessageV3>) translateMethod.invoke(decoder, payload);

            if (result == null || result.isEmpty()) {
                serdesMetric.addInc(context.channel().remoteAddress(), context.channel().localAddress());
            } else {
                serdesMetric.addTx(context.channel().remoteAddress(), context.channel().localAddress());
                transmitter.transmit(result, ((Supplier<BmpMessage.Identities>) idSupplier.get(decoder)).get());
            }
        }
    }
}
