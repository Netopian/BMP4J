package netopian.network.protocol.bmp4j.serdes.wellknown.attribute;

import static netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder.AFI_IP;
import static netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder.AFI_IP6;
import static netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder.NOT_SET;

import netopian.network.protocol.bmp4j.serdes.concept.AttributeDecoder;
import netopian.network.protocol.bmp4j.serdes.concept.NlriDecoder;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint8;

import com.google.protobuf.Any;

import gobgpapi.Attribute;
import gobgpapi.Gobgp;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MpReachNLRIAttributeDecoder implements AttributeDecoder {

    /*
     * +---------------------------------------------------------+
     * | Address Family Identifier (2 octets) ------------------ |
     * +---------------------------------------------------------+
     * | Subsequent Address Family Identifier (1 octet) -------- |
     * +---------------------------------------------------------+
     * | Length of Next Hop Network Address (1 octet) ---------- |
     * +---------------------------------------------------------+
     * | Network Address of Next Hop (variable) ---------------- |
     * +---------------------------------------------------------+
     * | Reserved (1 octet) ------------------------------------ |
     * +---------------------------------------------------------+
     * | Network Layer Reachability Information (variable) ----- |
     * +---------------------------------------------------------+
     */

    private static final int MIN_LENGTH_LIMIT = 4;

    @Override
    public Attribute.MpReachNLRIAttribute translate(ByteBuf byteBuf) {

        if (byteBuf.readableBytes() < MIN_LENGTH_LIMIT) {
            log.warn("bad MpReachNLRIAttribute bytes count: {} which should be {}.", byteBuf.readableBytes(),
                MIN_LENGTH_LIMIT);
            byteBuf.release();
            return null;
        }

        Attribute.MpReachNLRIAttribute.Builder builder = Attribute.MpReachNLRIAttribute.newBuilder();

        int afi = Sequential.readUint16(byteBuf).intValue();
        int safi = Sequential.readUint8(byteBuf).intValue();
        int nextHopLen = Sequential.readUint8(byteBuf).intValue();

        if (byteBuf.readableBytes() < nextHopLen) {
            log.warn("bad MpReachNLRIAttribute next hop: {} which should be {}.", byteBuf.readableBytes(), nextHopLen);
            byteBuf.release();
            return null;
        } else if (nextHopLen > 0) {
            ByteBuf nextHops = byteBuf.readBytes(nextHopLen);
            // IPAddressPrefix represents the NLRI for: AFI=1, SAFI=1 / AFI=2, SAFI=1
            if (nextHopLen == 4 && afi == AFI_IP) {
                builder.addNextHops(Address.uint32ToIpv4(Sequential.readUint32(nextHops)));
            } else if (nextHopLen == 16 && afi == AFI_IP6) {
                Uint8[] temp = new Uint8[16];
                for (int i = 0; i < 16; i++) {
                    temp[i] = Sequential.readUint8(nextHops);
                }
                builder.addNextHops(Address.Uint8sToIpv6(temp));
            } else {
                log.info(
                    "Caution: unsupported MpReachNLRI family (afi {}, safi {}), or invalid next hop byte length {}",
                    afi, safi, nextHopLen);
                nextHops.release();
            }
        }

        if (byteBuf.isReadable()) {
            Sequential.readUint8(byteBuf);
            NlriDecoder.Helper.getDecoderWithKey(afi << 32 + safi << 8 + NOT_SET)
                .translate(byteBuf)
                .forEach((nlri) -> builder.addNlris(Any.pack(nlri)));
        }

        return builder.setFamily(Gobgp.Family.newBuilder().setAfiValue(afi).setSafiValue(safi).build()).build();
    }

    @Override
    public int attrType() {
        return ATTR_TYPE_MP_REACH_NLRI;
    }

    @Override
    public int attrFlag() {
        return ATTR_FLAG_OPTIONAL;
    }
}
