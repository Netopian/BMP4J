package netopian.network.protocol.bmp4j.serdes.concept;

import com.google.protobuf.GeneratedMessageV3;

import io.netty.buffer.ByteBuf;


public interface SingleDecoder extends PBaseDecoder {

    <T extends GeneratedMessageV3> T translate(ByteBuf byteBuf);

}
