package netopian.network.protocol.bmp4j.serdes;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


@Component
public class TcpSplitterSupplier implements Supplier<TcpSplitterSupplier.TcpPackageSplitter> {

    @Override
    public TcpPackageSplitter get() {
        return new TcpPackageSplitter();
    }

    public static class TcpPackageSplitter extends LengthFieldBasedFrameDecoder {
        private static final int MAX_FRAME_SIZE = 65535;

        private static final int VERSION_SIZE = 1;

        private static final int LENGTH_SIZE = 4;

        /*
         * 0 1 2 3 4 5 6 7 8 1 2 3 4 5 6 7 8 1 2 3 4 5 6 7 8 1 2 3 4 5 6 7 8
         * +-+-+-+-+-+-+-+-+-
         * |___ Version ____|
         * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         * | Message Length |
         * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         * |___ Msg Type ___|
         * +----------------+
         */

        public TcpPackageSplitter() {
            super(MAX_FRAME_SIZE, VERSION_SIZE, LENGTH_SIZE, -VERSION_SIZE - LENGTH_SIZE, 0);
        }

    }

}
