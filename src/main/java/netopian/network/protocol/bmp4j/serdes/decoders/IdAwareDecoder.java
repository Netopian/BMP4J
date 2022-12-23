package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.Objects;
import java.util.function.Supplier;

import netopian.network.protocol.bmp4j.model.BmpMessage;
import netopian.network.protocol.bmp4j.serdes.concept.MultipleDecoder;

import io.netty.channel.ChannelHandlerContext;

public abstract class IdAwareDecoder implements MultipleDecoder {
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
    private static final String[] MESSAGE_NAMES =
        {"RouteMonitoringMessage", "StatsReportsMessage", "PeerDownNotification", "PeerUpNotification",
            "InitiationMessage", "TerminationMessage", "RouteMirroringMessage"};

    private ChannelHandlerContext context;

    public Supplier<BmpMessage.Identities> idSupplier = () -> BmpMessage.Identities.newBuilder()
        .setRemoteAddress(context.channel().remoteAddress().toString())
        .setMessageName(MESSAGE_NAMES[getMessageType()])
        .build();

    protected abstract int getMessageType();

    public IdAwareDecoder(ChannelHandlerContext context) {
        this.context = Objects.requireNonNull(context, "invalid channel handler context.");
    }

}
