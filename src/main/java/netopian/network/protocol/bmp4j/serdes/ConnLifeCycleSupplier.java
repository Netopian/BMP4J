package netopian.network.protocol.bmp4j.serdes;

import java.util.function.Supplier;

import netopian.network.protocol.bmp4j.statistic.UpStreamMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
public class ConnLifeCycleSupplier implements Supplier<ConnLifeCycleSupplier.ConnectionLifeCycle> {

    @Autowired
    private UpStreamMetric upStreamMetric;

    @Override
    public ConnectionLifeCycle get() {
        return new ConnectionLifeCycle(upStreamMetric);
    }

    @AllArgsConstructor
    @Slf4j
    public static class ConnectionLifeCycle extends ChannelDuplexHandler {

        private UpStreamMetric upStreamMetric;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            upStreamMetric.activeChannel(ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            upStreamMetric.deActiveChannel(ctx.channel());
            super.channelInactive(ctx);
        }
    }
}
