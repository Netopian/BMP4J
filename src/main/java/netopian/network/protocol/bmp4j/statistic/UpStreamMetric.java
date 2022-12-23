package netopian.network.protocol.bmp4j.statistic;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UpStreamMetric {

    private static ConcurrentHashMap<ChannelPeer, ChannelTimer> activeConnections = new ConcurrentHashMap<>();

    public void activeChannel(@NotNull(message = "can NOT active invalid channel") @Valid Channel channel) {
        ChannelPeer channelPeer = new ChannelPeer(channel.remoteAddress(), channel.localAddress());
        if (!activeConnections.containsKey(channelPeer)) {
            activeConnections.put(channelPeer, new ChannelTimer(channel, System.currentTimeMillis()));
        }

        ChannelTimer connection = activeConnections.get(channelPeer);
        connection.setLastActive(System.currentTimeMillis());
        log.warn("received activity operation on channel: {}", connection);
    }

    public void deActiveChannel(@NotNull(message = "can NOT de-active invalid channel") @Valid Channel channel) {
        ChannelPeer channelPeer = new ChannelPeer(channel.remoteAddress(), channel.localAddress());

        if (activeConnections.containsKey(channelPeer)) {
            ChannelTimer connection = activeConnections.get(channelPeer);
            connection.setLastDeActive(System.currentTimeMillis());
            log.warn("received inactivity operation on channel: {}", connection);
        }
    }

    public Collection<ChannelTimer> getConnections() {
        return activeConnections.values();
    }

    public ChannelTimer getConnection(SocketAddress remote, SocketAddress local) {
        return activeConnections.get(new ChannelPeer(remote, local));
    }

    public void removeConnection(SocketAddress remote, SocketAddress local) {
        ChannelTimer connection = activeConnections.get(new ChannelPeer(remote, local));
        if (connection == null) {
            log.info("can not find connection with remote: " + remote + " and local: " + local);
            return;
        }

        activeConnections.remove(new ChannelPeer(remote, local));
        connection.getChannel().close();
        log.warn("force rebooting on channel: {}", connection);
    }

    @ToString
    @Getter
    @Setter
    public final class ChannelTimer {
        private final Channel channel;

        private final long createTime;

        private long lastActive = 0;

        private long lastDeActive = 0;

        private ChannelTimer(Channel channel, long createTime) {
            this.channel = channel;
            this.createTime = createTime;
        }
    }
}
