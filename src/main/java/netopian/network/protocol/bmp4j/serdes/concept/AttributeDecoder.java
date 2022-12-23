package netopian.network.protocol.bmp4j.serdes.concept;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

public interface AttributeDecoder extends SingleDecoder {

    int ATTR_TYPE_ORIGIN = 1;

    int ATTR_TYPE_AS_PATH = 2;

    int ATTR_TYPE_NEXT_HOP = 3;

    int ATTR_TYPE_MULTI_EXIT_DISC = 4;

    int ATTR_TYPE_LOCAL_PREF = 5;

    int ATTR_TYPE_ATOMIC_AGGREGATE = 6;

    int ATTR_TYPE_AGGREGATOR = 7;

    int ATTR_TYPE_COMMUNITIES = 8;

    int ATTR_TYPE_ORIGINATOR_ID = 9;

    int ATTR_TYPE_CLUSTER_LIST = 10;

    int ATTR_TYPE_MP_REACH_NLRI = 14;

    int ATTR_TYPE_MP_UNREACH_NLRI = 15;

    int ATTR_TYPE_EXTENDED_COMMUNITIES = 16;

    int ATTR_TYPE_AS4_PATH = 17;

    int ATTR_TYPE_AS4_AGGREGATOR = 18;

    int ATTR_TYPE_PMSI_TUNNEL = 22;

    int ATTR_TYPE_TUNNEL_ENCAP = 23;

    int ATTR_TYPE_IP6_EXTENDED_COMMUNITIES = 25;

    int ATTR_TYPE_AIGP = 26;

    int ATTR_TYPE_LS = 29;

    int ATTR_TYPE_LARGE_COMMUNITY = 32;

    // undefined in PB messages
    int ATTR_TYPE_PREFIX_SID = 40;

    int ATTR_FLAG_EXTENDED_LENGTH = 1 << 4;

    int ATTR_FLAG_PARTIAL = 1 << 5;

    int ATTR_FLAG_TRANSITIVE = 1 << 6;

    int ATTR_FLAG_OPTIONAL = 1 << 7;

    int attrType();

    int attrFlag();

    default void register() {
        AttributeDecoder.Helper.extendedDecoders.put(attrType(), this);
    }

    @Slf4j
    class Helper {
        private static final String wellKnownPackage = "com.huawei.network.protocol.bmp4j.serdes.wellknown.attribute.";

        private static final String[] wellKnownNames = {"AggregatorAttributeDecoder", "AigpAttributeDecoder",
            "As4AggregatorAttributeDecoder", "As4PathAttributeDecoder", "AsPathAttributeDecoder",
            "AtomicAggregateAttributeDecoder", "ClusterListAttributeDecoder", "CommunitiesAttributeDecoder",
            "ExtendedCommunitiesAttributeDecoder", "IP6ExtendedCommunitiesAttributeDecoder",
            "LargeCommunitiesAttributeDecoder", "LocalPrefAttributeDecoder", "LsAttributeDecoder",
            "MpReachNLRIAttributeDecoder", "MpUnreachNLRIAttributeDecoder", "MultiExitDiscAttributeDecoder",
            "NextHopAttributeDecoder", "OriginatorIdAttributeDecoder", "OriginAttributeDecoder",
            "PmsiTunnelAttributeDecoder", "TunnelEncapAttributeDecoder"};

        private static final Map<Integer, AttributeDecoder> wellKnownDecoders = new HashMap<>();

        private static final Map<Integer, AttributeDecoder> extendedDecoders = new HashMap<>();

        static {
            try {
                for (String decoderName : wellKnownNames) {
                    AttributeDecoder temp = (AttributeDecoder) Class.forName(wellKnownPackage + decoderName)
                        .getDeclaredConstructor()
                        .newInstance();
                    wellKnownDecoders.put(temp.attrType(), temp);
                    log.info("register attribute decoder {} with type {}.", decoderName, temp.attrType());
                }
            } catch (ReflectiveOperationException e) {
                log.warn("failed to register attribute decoder with error: {}. serdes may not work properly",
                    e.getMessage());
            }
        }

        public static void registerExtendedDecoder(AttributeDecoder extDecoder) {
            extendedDecoders.putIfAbsent(extDecoder.attrType(), extDecoder);
        }

        public static AttributeDecoder getDecoderWithType(int flag, int type) {
            if (wellKnownDecoders.containsKey(type) && wellKnownDecoders.get(type).attrFlag() == flag) {
                return wellKnownDecoders.get(type);
            } else if (extendedDecoders.containsKey(type) && extendedDecoders.get(type).attrFlag() == flag) {
                return extendedDecoders.get(type);
            }
            try {
                return (AttributeDecoder) Class.forName(wellKnownPackage + "UnknownAttributeDecoder")
                    .getDeclaredConstructor(Integer.class, Integer.class)
                    .newInstance(type, flag);
            } catch (ReflectiveOperationException e) {
                log.warn("failed to register attribute decoder with error: {}. serdes may not work properly",
                    e.getMessage());
                return null;
            }
        }
    }

}
