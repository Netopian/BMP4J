package netopian.network.protocol.bmp4j.serdes.wellknown.nlri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint8;

import gobgpapi.Attribute;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IP4UnicastPrefixDecoder implements NlriDecoder {

    @Override
    public Collection<Attribute.IPAddressPrefix> translate(ByteBuf byteBuf) {
        List<Attribute.IPAddressPrefix> result = new ArrayList<>();

        while (byteBuf.isReadable()) {
            int length = Sequential.readUint8(byteBuf).intValue();
            int byteLen = length / 8 + (length % 8 == 0 ? 0 : 1);
            if (byteLen > 4 || byteLen > byteBuf.readableBytes()) {
                log.warn("IP4UnicastPrefix length {} is out of range. {} bytes remains.", byteLen,
                    byteBuf.readableBytes());
                byteBuf.release();
                break;
            }
            Uint8[] temp = new Uint8[4];
            for (int i = 0; i < 4; i++) {
                if (i < byteLen) {
                    temp[i] = Sequential.readUint8(byteBuf);
                } else {
                    temp[i] = Uint8.valueOf(0);
                }
            }

            result.add(Attribute.IPAddressPrefix.newBuilder()
                .setPrefixLen(length)
                .setPrefix(Address.Uint8sToIpv4(temp))
                .build());
        }

        return result;
    }

    @Override
    public int afi() {
        return AFI_IP;
    }

    @Override
    public int safi() {
        return SAFI_UNICAST;
    }
}
