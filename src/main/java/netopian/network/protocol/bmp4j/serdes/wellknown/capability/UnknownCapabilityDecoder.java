package netopian.network.protocol.bmp4j.serdes.wellknown.capability;

import com.google.protobuf.ByteString;

import gobgpapi.Capability;
import netopian.network.protocol.bmp4j.serdes.concept.CapabilityDecoder;
import netopian.network.protocol.bmp4j.utils.Sequential;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UnknownCapabilityDecoder implements CapabilityDecoder {

    private final Integer capabilityCode;

    public UnknownCapabilityDecoder(Integer capabilityCode) {
        this.capabilityCode = capabilityCode;
    }

    @Override
    public Capability.UnknownCapability translate(ByteBuf buffer) {
        log.info("get an unknown capability in code: {}", capabilityCode);
        return Capability.UnknownCapability.newBuilder()
            .setCode(capabilityCode())
            .setValue(ByteString.copyFromUtf8(Sequential.read2UTF8(buffer, buffer.readableBytes())))
            .build();
    }

    @Override
    public int capabilityCode() {
        return capabilityCode;
    }
}
