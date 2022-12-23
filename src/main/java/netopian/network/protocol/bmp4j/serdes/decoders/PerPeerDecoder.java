package netopian.network.protocol.bmp4j.serdes.decoders;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.model.BmpMessage;
import netopian.network.protocol.bmp4j.utils.Address;
import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
abstract class PerPeerDecoder extends IdAwareDecoder {

    private static final int PER_PEER_HEADER_SIZE = 42;

    protected Supplier<BmpMessage.PeerHeader> headerSupplier;

    protected Supplier<Boolean> isIPv4Supplier;

    public PerPeerDecoder(ChannelHandlerContext context) {
        super(context);
    }

    private void headerFirst(ByteBuf byteBuf, BmpMessage.PeerHeader.Builder builder) {
        /*
         * Peer Type (1 byte): Identifies the type of peer. Currently, three types of peers are identified:
         * Peer Type = 0: Global Instance Peer
         * Peer Type = 1: RD Instance Peer
         * Peer Type = 2: Local Instance Peer
         * Section 4.1 of [RFC9069] defines A new peer type for Loc-RIB to indicate that it represents
         * the router Loc-RIB, which may have a route distinguisher (RD).
         * * Peer Type = 3: Loc-RIB Instance Peer
         */
        BmpMessage.PeerType peerType = BmpMessage.PeerType.forNumber(Sequential.readUint8(byteBuf).intValue() + 1);
        builder.setPeerType(peerType);

        /*
         * Peer Flags (1 byte): These flags provide more information about the peer. The flags are defined as follows:
         * 0 1 2 3 4 5 6 7
         * +-+-+-+-+-+-+-+-+
         * |V|L|A|O| Resv |
         * +-+-+-+-+-+-+-+-+
         * The per-peer header flags for the Loc-RIB Instance Peer Type are defined as follows:
         * 0 1 2 3 4 5 6 7
         * +-+-+-+-+-+-+-+-+
         * |F| | | | | | | |
         * +-+-+-+-+-+-+-+-+
         * The V flag is not applicable with the Loc-RIB Instance Peer Type considering Peer Address are zero-filled.
         */
        int flags = Sequential.readUint8(byteBuf).intValue();
        switch (peerType) {
            case PeerType_LOCRIB:
                builder.setRibTypeValue(4 + ((flags & 0xff) >>> 7) + 1);
                break;
            case PeerType_GLOBAL:
            case PeerType_L3VPN:
            case PeerType_LOCAL:
                builder.setIsIpv4((flags & 0xff) >>> 7 == 0)
                    .setRibTypeValue(((flags << 3) & 0xff >>> 7) * 2 + ((flags << 1) & 0xff >>> 7) + 1);
                break;
            default:
                log.warn("unknown peer type: {}", peerType);
        }

        builder.setPeerDistinguisher(
            Sequential.readUint32(byteBuf).intValue() + ":" + Sequential.readUint32(byteBuf).intValue());

        /*
         * set ip address v4/v6
         */
        if (builder.getIsIpv4()) {
            for (int i = 0; i < 4; i++) {
                if (i != 3) {
                    Sequential.readUint32(byteBuf);
                } else {
                    builder.setAddress(Address.uint32ToIpv4(Sequential.readUint32(byteBuf)));
                }
            }
        } else {
            Uint16[] temp = new Uint16[8];
            for (int i = 0; i < 8; i++) {
                temp[i] = Sequential.readUint16(byteBuf);
                builder.setAddress(Address.Uint16sToIpv6(temp));
            }
        }

        builder.setAs(Sequential.readUint32(byteBuf).intValue())
            .setBgpId(Address.uint32ToIpv4(Sequential.readUint32(byteBuf)))
            .setTimestampSec(Sequential.readUint32(byteBuf).intValue())
            .setTimestampMicro(Sequential.readUint32(byteBuf).intValue());
    }

    @Override
    public Collection<GeneratedMessageV3> translate(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < PER_PEER_HEADER_SIZE) {
            log.warn("peer header length {} is out of range: {}", byteBuf.readableBytes(), PER_PEER_HEADER_SIZE);
            byteBuf.release();
            return Collections.emptyList();
        }
        BmpMessage.PeerHeader.Builder builder = BmpMessage.PeerHeader.newBuilder();
        ByteBuf buf = byteBuf.readBytes(PER_PEER_HEADER_SIZE);
        headerFirst(buf, builder);
        buf.release();
        headerSupplier = () -> builder.build();
        isIPv4Supplier = () -> builder.getIsIpv4();
        Collection<GeneratedMessageV3> messages = subTranslate(byteBuf);
        if (byteBuf.isReadable()) {
            byteBuf.release();
        }
        return messages;
    }

    protected abstract <T extends GeneratedMessageV3> Collection<T> subTranslate(ByteBuf byteBuf);
}
