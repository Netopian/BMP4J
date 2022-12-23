package netopian.network.protocol.bmp4j.serdes.wellknown.pdu;

import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.serdes.concept.BgpPduDecoder;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BgpUnknownPduDecoder implements BgpPduDecoder {
    @Override
    public int pduType() {
        return BgpPduDecoder.TYPE_UNKNOWN;
    }

    @Override
    public GeneratedMessageV3 translate(ByteBuf byteBuf) {
        log.warn("can not translate without pdu schema. drop as default.");
        byteBuf.release();
        return null;
    }
}
