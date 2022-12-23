package netopian.network.protocol.bmp4j.initial;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import netopian.network.protocol.bmp4j.config.BmpServerExtConfig;
import netopian.network.protocol.bmp4j.serdes.ConnLifeCycleSupplier;
import netopian.network.protocol.bmp4j.serdes.PbDecoderSupplier;
import netopian.network.protocol.bmp4j.serdes.TcpSplitterSupplier;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BmpListenerInitializer {

    private final BmpServerExtConfig bmpServerExtConfig;

    @Autowired
    private TcpSplitterSupplier tcpSplitterSupplier;

    @Autowired
    private PbDecoderSupplier pbDecoderSupplier;

    @Autowired
    private ConnLifeCycleSupplier connLifeCycleSupplier;

    public BmpListenerInitializer(BmpServerExtConfig bmpServerExtConfig) {
        this.bmpServerExtConfig = bmpServerExtConfig;
    }

    @PostConstruct
    private void initializeBeans() {
        if (Objects.isNull(bmpServerExtConfig)) {
            return;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // default parallel = core x 2
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 127)
            .option(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer() {
                @Override
                public void initChannel(io.netty.channel.Channel ch) {
                    ch.pipeline().addLast("lifeCycle", connLifeCycleSupplier.get());
                    ch.pipeline().addLast("splitter", tcpSplitterSupplier.get());
                    ch.pipeline().addLast("decoder", pbDecoderSupplier.get());
                }
            });
        try {
            bootstrap.bind(bmpServerExtConfig.getListenOn().toSocketAddress()).sync();
        } catch (InterruptedException e) {
            log.error("Netty server shutdown with error: {}", e.getMessage());
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }

        log.info("Netty server is working on: {}", bmpServerExtConfig.getListenOn());
    }
}
