package netopian.network.protocol.bmp4j.serdes.concept;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

public interface BgpPduDecoder extends SingleDecoder {
    int TYPE_UNKNOWN = 0;

    int TYPE_OPEN = 1;

    int TYPE_UPDATE = 2;

    int TYPE_NOTIFICATION = 3;

    int TYPE_KEEPALIVE = 4;

    int MARKER_LEN = 16;

    int LENGTH_LEN = 2;

    int TYPE_LEN = 1;

    int MIN_LENGTH = MARKER_LEN + LENGTH_LEN + TYPE_LEN;

    int pduType();

    @Slf4j
    class Helper {
        private static final String wellKnownPackage = "com.huawei.network.protocol.bmp4j.serdes.wellknown.pdu.";

        private static final String[] wellKnownNames = {"BgpOpenPduDecoder", "BgpUpdatePduDecoder"};

        private static final Map<Integer, BgpPduDecoder> wellKnownPduDecoders = new HashMap<>();

        private static BgpPduDecoder pduDecodeKeeper;

        static {
            try {
                for (String decoderName : wellKnownNames) {
                    BgpPduDecoder temp = (BgpPduDecoder) Class.forName(wellKnownPackage + decoderName)
                        .getDeclaredConstructor()
                        .newInstance();
                    wellKnownPduDecoders.put(temp.pduType(), temp);
                    log.info("register pdu decoder {} with key {}.", decoderName, temp.pduType());
                }
                pduDecodeKeeper = (BgpPduDecoder) Class.forName(wellKnownPackage + "BgpUnknownPduDecoder")
                    .getDeclaredConstructor()
                    .newInstance();
                log.info("register default pdu decoder.");
            } catch (ReflectiveOperationException e) {
                log.warn("failed to register pdu decoder with error: {}. serdes may not work properly", e.getMessage());
            }
        }

        public static BgpPduDecoder getDecoderWithKey(int key) {
            if (wellKnownPduDecoders.containsKey(key)) {
                return wellKnownPduDecoders.get(key);
            } else {
                return pduDecodeKeeper;
            }
        }
    }
}
