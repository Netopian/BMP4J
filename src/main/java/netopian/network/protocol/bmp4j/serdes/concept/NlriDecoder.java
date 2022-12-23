package netopian.network.protocol.bmp4j.serdes.concept;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

public interface NlriDecoder extends MultipleDecoder {
    int NOT_SET = 0;

    int SAFI_UNICAST = 1;

    int SAFI_MULTICAST = 2;

    int SAFI_MPLS_LABEL = 4;

    int SAFI_ENCAPSULATION = 7;

    int SAFI_VPLS = 65;

    int SAFI_EVPN = 70;

    int SAFI_LS = 71;

    int SAFI_SR_POLICY = 73;

    int SAFI_MPLS_VPN = 128;

    int SAFI_MPLS_VPN_MULTICAST = 129;

    int SAFI_ROUTE_TARGET_CONSTRAINTS = 132;

    int SAFI_FLOW_SPEC_UNICAST = 133;

    int SAFI_FLOW_SPEC_VPN = 134;

    int SAFI_KEY_VALUE = 241;

    int AFI_IP = 1;

    int AFI_IP6 = 2;

    int AFI_L2VPN = 25;

    int AFI_LS = 16388;

    int AFI_OPAQUE = 16397;

    int EVPN_ETHERNET_AUTO_DISCOVERY = 1;

    int EVPN_MAC_IP_ADVERTISEMENT = 2;

    int EVPN_INCLUSIVE_MCAST_ETHERNET = 3;

    int EVPN_ETHERNET_SEGMENT_ROUTE = 4;

    int EVPN_IP_PREFIX = 5;

    int EVPN_I_PMSI = 9;

    int afi();

    int safi();

    default int optionalEvpnRouteType() {
        return NOT_SET;
    }

    default int nlriKey() {
        return ((afi() & 0xffff) << 16) + ((safi() & 0xff) << 8) + (optionalEvpnRouteType() & 0xff);
    }

    default void register() {
        NlriDecoder.Helper.extendedDecoders.put(nlriKey(), this);
    }

    @Slf4j
    class Helper {
        private static final String wellKnownPackage = "com.huawei.network.protocol.bmp4j.serdes.wellknown.nlri.";

        private static final String[] wellKnownNames = {"IP4UnicastPrefixDecoder", "IP6UnicastPrefixDecoder"};

        private static final Map<Integer, NlriDecoder> wellKnownDecoders = new HashMap<>();

        private static final Map<Integer, NlriDecoder> extendedDecoders = new HashMap<>();

        private static NlriDecoder nlriDecodeKeeper;

        static {
            try {
                for (String decoderName : wellKnownNames) {
                    NlriDecoder temp = (NlriDecoder) Class.forName(wellKnownPackage + decoderName)
                        .getDeclaredConstructor()
                        .newInstance();
                    wellKnownDecoders.put(temp.nlriKey(), temp);
                    log.info("register nlri decoder {} with key {}.", decoderName, temp.nlriKey());
                }
                nlriDecodeKeeper = (NlriDecoder) Class.forName(wellKnownPackage + "UnknownNlriDecoder")
                    .getDeclaredConstructor()
                    .newInstance();
                log.info("register default nlri decoder.");
            } catch (ReflectiveOperationException e) {
                log.warn("failed to register nlri decoder with error: {}. serdes may not work properly",
                    e.getMessage());
            }
        }

        public static void registerExtendedDecoder(NlriDecoder extDecoder) {
            extendedDecoders.putIfAbsent(extDecoder.nlriKey(), extDecoder);
        }

        public static NlriDecoder getDecoderWithKey(int key) {
            if (wellKnownDecoders.containsKey(key)) {
                return wellKnownDecoders.get(key);
            } else if (extendedDecoders.containsKey(key)) {
                return extendedDecoders.get(key);
            } else {
                return nlriDecodeKeeper;
            }
        }
    }

}
