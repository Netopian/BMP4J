package netopian.network.protocol.bmp4j.serdes.wellknown.nlri;

import java.util.Collection;
import java.util.Collections;

import netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UnknownNlriDecoder implements NlriDecoder {
    @Override
    public Collection translate(ByteBuf byteBuf) {
        log.warn("can not translate without nlri schema. drop as default.");
        byteBuf.release();
        return Collections.emptyList();
    }

    @Override
    public int afi() {
        return NOT_SET;
    }

    @Override
    public int safi() {
        return NOT_SET;
    }
}
