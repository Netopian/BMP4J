package netopian.network.protocol.bmp4j.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "bmp.server")
@Setter
@Getter
@ToString
public class BmpServerExtConfig {

    private ListenOn listenOn;

    private int seqPrefix = 0;

    private Model model;

    @Setter
    @Getter
    @ToString
    public static final class ListenOn {

        private String ip;

        private int port;

        public SocketAddress toSocketAddress() {
            return AccessController
                .doPrivileged((PrivilegedAction<InetSocketAddress>) () -> new InetSocketAddress(ip, port));
        }
    }

    @Setter
    @Getter
    @ToString
    public static final class Model {

        private int version = 0;

        private int subVersion = 0;

        public Long toFullVersion() {
            return ((long) version << 32) + (long) subVersion;
        }
    }
}
