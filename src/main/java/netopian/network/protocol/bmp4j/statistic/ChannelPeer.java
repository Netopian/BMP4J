package netopian.network.protocol.bmp4j.statistic;

import java.net.SocketAddress;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@AllArgsConstructor
@ToString
@EqualsAndHashCode
final class ChannelPeer {
    private final SocketAddress remoteSocket;

    private final SocketAddress localSocket;
}
