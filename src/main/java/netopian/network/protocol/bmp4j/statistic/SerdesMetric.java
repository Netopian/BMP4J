package netopian.network.protocol.bmp4j.statistic;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SerdesMetric {

    private static ConcurrentHashMap<ChannelPeer, MessageCounter> activeConnections = new ConcurrentHashMap<>();

    public Collection<MessageCounter> getCounters() {
        return activeConnections.values();
    }

    public MessageCounter getCounter(SocketAddress remote, SocketAddress local) {
        return activeConnections.get(new ChannelPeer(remote, local));
    }

    public void removeCounter(@NotNull(message = "NO statistic on invalid remote socket") @Valid SocketAddress remote,
        @NotNull(message = "NO statistic on invalid local socket") @Valid SocketAddress local) {
        if (activeConnections.remove(new ChannelPeer(remote, local)) == null) {
            log.info("can not find counter with remote: " + remote + " and local: " + local);
        }
    }

    public void addRx(@NotNull(message = "NO statistic on invalid remote socket") @Valid SocketAddress remote,
        @NotNull(message = "NO statistic on invalid local socket") @Valid SocketAddress local) {
        ChannelPeer channelPeer = new ChannelPeer(remote, local);
        if (!activeConnections.containsKey(channelPeer)) {
            activeConnections.put(channelPeer, new MessageCounter(remote.toString(), local.toString()));
        }
        activeConnections.get(channelPeer).getRxCounter().incrementAndGet();
    }

    public void addTx(@NotNull(message = "NO statistic on invalid remote socket") @Valid SocketAddress remote,
        @NotNull(message = "NO statistic on invalid local socket") @Valid SocketAddress local) {
        ChannelPeer channelPeer = new ChannelPeer(remote, local);
        if (!activeConnections.containsKey(channelPeer)) {
            activeConnections.put(channelPeer, new MessageCounter(remote.toString(), local.toString()));
        }
        activeConnections.get(channelPeer).getTxCounter().incrementAndGet();
    }

    public void addInc(@NotNull(message = "NO statistic on invalid remote socket") @Valid SocketAddress remote,
        @NotNull(message = "NO statistic on invalid local socket") @Valid SocketAddress local) {
        ChannelPeer channelPeer = new ChannelPeer(remote, local);
        if (!activeConnections.containsKey(channelPeer)) {
            activeConnections.put(channelPeer, new MessageCounter(remote.toString(), local.toString()));
        }
        activeConnections.get(channelPeer).getIncCounter().incrementAndGet();
    }

    @Getter
    @Setter
    public final class MessageCounter {
        private final String remote;

        private final String local;

        private AtomicLong rxCounter = new AtomicLong();

        private AtomicLong txCounter = new AtomicLong();

        private AtomicLong incCounter = new AtomicLong();

        public MessageCounter(String remote, String local) {
            this.remote = remote;
            this.local = local;
        }
    }

}
