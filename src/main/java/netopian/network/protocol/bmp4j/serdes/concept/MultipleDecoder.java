package netopian.network.protocol.bmp4j.serdes.concept;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import io.netty.buffer.ByteBuf;

public interface MultipleDecoder extends PBaseDecoder {

    <T extends GeneratedMessageV3> Collection<T> translate(ByteBuf byteBuf);

}
