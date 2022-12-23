package netopian.network.protocol.bmp4j.serdes.concept;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

public interface CapabilityDecoder extends SingleDecoder {

    int capabilityCode();

    default void register() {
        CapabilityDecoder.Helper.extendedDecoders.put(capabilityCode(), this);
    }

    @Slf4j
    public class Helper {
        private static final String wellKnownPackage = "com.huawei.network.protocol.bmp4j.serdes.wellknown.capability.";

        private static final String[] wellKnownNames =
            {"AddPathCapabilityDecoder", "CarryingLabelInfoCapabilityDecoder", "EnhancedRouteRefreshCapabilityDecoder",
                "ExtendedNextHopCapabilityDecoder", "FourOctetASNumberCapabilityDecoder", "FQDNCapabilityDecoder",
                "GracefulRestartCapabilityDecoder", "LongLivedGracefulRestartCapabilityDecoder",
                "MultiPProtocolCapabilityDecoder", "RouteRefreshCapabilityDecoder"};

        private static final Map<Integer, CapabilityDecoder> wellKnownDecoders = new HashMap<>();

        private static final Map<Integer, CapabilityDecoder> extendedDecoders = new HashMap<>();

        static {
            try {
                for (String decoderName : wellKnownNames) {
                    CapabilityDecoder temp = (CapabilityDecoder) Class.forName(wellKnownPackage + decoderName)
                        .getDeclaredConstructor()
                        .newInstance();
                    wellKnownDecoders.put(temp.capabilityCode(), temp);
                    log.info("register capability decoder {} with capId {}.", decoderName, temp.capabilityCode());
                }
            } catch (ReflectiveOperationException e) {
                log.info("failed to register capability decoder with error: {}. serdes may not work properly",
                    e.getMessage());
            }
        }

        public static void registerExtendedDecoder(CapabilityDecoder extDecoder) {
            extendedDecoders.putIfAbsent(extDecoder.capabilityCode(), extDecoder);
        }

        public static CapabilityDecoder getDecoderWithKey(int key) {
            if (wellKnownDecoders.containsKey(key)) {
                return wellKnownDecoders.get(key);
            } else if (extendedDecoders.containsKey(key)) {
                return extendedDecoders.get(key);
            }
            try {
                return (CapabilityDecoder) Class.forName(wellKnownPackage + "UnknownCapabilityDecoder")
                    .getDeclaredConstructor(Integer.class)
                    .newInstance(key);
            } catch (ReflectiveOperationException e) {
                log.info("failed to instant default cap decoder: {}. serdes may not work properly", e.getMessage());
                return null;
            }

        }
    }

}
