package netopian.network.protocol.bmp4j.serdes.decoders;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import netopian.network.protocol.bmp4j.utils.Sequential;
import netopian.network.protocol.bmp4j.utils.Uint16;
import netopian.network.protocol.bmp4j.utils.Uint32;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class StatsReportsDecoder extends PerPeerDecoder {

    private static final int TUPLE_MIN_LEN = 4;

    private static final int UINT32_BYTES_CNT = 4;

    private static final int UINT64_BYTES_CNT = 8;

    private static final int FAMILY_BYTES_CNT = 3;

    private static final int HANDLE_MSG_TYPE = 1;

    @Override
    protected int getMessageType() {
        return HANDLE_MSG_TYPE;
    }

    public StatsReportsDecoder(ChannelHandlerContext context) {
        super(context);
    }

    @Override
    protected List<BmpMessage.StatsReportsMessage> subTranslate(ByteBuf byteBuf) {
        Uint32 count = Sequential.readUint32(byteBuf);
        if (count.toUint64().unsignedLongValue().compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            log.warn("stats counters {} is out of range: {}", count, Integer.MAX_VALUE);
            byteBuf.release();
            return Collections.emptyList();
        }

        int round = count.intValue();
        List<BmpMessage.StatsReportsMessage> result = new ArrayList<>(round);
        while (round-- > 0 && byteBuf.readableBytes() >= TUPLE_MIN_LEN) {
            int type = Sequential.readUint16(byteBuf).intValue();
            Uint16 length = Sequential.readUint16(byteBuf);
            switch (type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 11:
                case 12:
                case 13:
                    if (byteBuf.readableBytes() < UINT32_BYTES_CNT) {
                        log.warn(
                            "incomplete StatsReportsMessage translation. decode length is: {}, but {} bytes remain.",
                            UINT32_BYTES_CNT, byteBuf.readableBytes());
                        byteBuf.release();
                        return result;
                    }
                    result.add(BmpMessage.StatsReportsMessage.newBuilder()
                        .setId(idSupplier.get())
                        .setPeerHeader(headerSupplier.get())
                        .addUniStatistic(BmpMessage.StatisticReport.newBuilder()
                            .setStatsTypeValue(type + 1)
                            .setCount(Sequential.readUint32(byteBuf).toUint64().longValue())
                            .build())
                        .build());
                    continue;
                case 7:
                case 8:
                    if (byteBuf.readableBytes() < UINT64_BYTES_CNT) {
                        log.warn(
                            "incomplete StatsReportsMessage translation. decode length is: {}, but {} bytes remain.",
                            UINT64_BYTES_CNT, byteBuf.readableBytes());
                        byteBuf.release();
                        return result;
                    }
                    result.add(BmpMessage.StatsReportsMessage.newBuilder()
                        .setId(idSupplier.get())
                        .setPeerHeader(headerSupplier.get())
                        .addUniStatistic(BmpMessage.StatisticReport.newBuilder()
                            .setStatsTypeValue(type + 1)
                            .setCount(Sequential.readUint64(byteBuf).longValue())
                            .build())
                        .build());
                    continue;
                case 9:
                case 10:
                    if (byteBuf.readableBytes() < UINT64_BYTES_CNT + FAMILY_BYTES_CNT) {
                        log.warn(
                            "incomplete StatsReportsMessage translation. decode length is: {}, but {} bytes remain.",
                            UINT64_BYTES_CNT + FAMILY_BYTES_CNT, byteBuf.readableBytes());
                        byteBuf.release();
                        return result;
                    }
                    result.add(BmpMessage.StatsReportsMessage.newBuilder()
                        .setId(idSupplier.get())
                        .setPeerHeader(headerSupplier.get())
                        .addUniStatistic(BmpMessage.StatisticReport.newBuilder()
                            .setStatsTypeValue(type + 1)
                            .setOptionalFamily(BmpMessage.Family.newBuilder()
                                .setAfi(Sequential.readUint16(byteBuf).intValue())
                                .setSafi(Sequential.readUint8(byteBuf).intValue())
                                .build())
                            .setCount(Sequential.readUint64(byteBuf).longValue())
                            .build())
                        .build());
                    continue;
                default:
                    log.warn("unknown StatsReportsType {}. drop {} bytes.", type, length.intValue());
                    Sequential.read2bytes(byteBuf, new byte[length.intValue()]);
            }
        }
        return result;
    }

}
