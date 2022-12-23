package netopian.network.protocol.bmp4j.utils;

import java.util.Objects;


public class Address {

    public static Uint32 ipv4ToUint32(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        String[] ipAddressInArray = ipAddress.split("\\.");
        if (ipAddressInArray.length != 4) {
            throw new IllegalArgumentException("invalid ipv4 string format: " + ipAddress);
        }
        long result = 0;
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return Uint32.valueOf(result);
    }

    public static String uint32ToIpv4(Uint32 uint32) {
        long i = uint32.longValue();
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

    public static Uint8[] ipv4ToUint8s(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        String[] split = ipAddress.split("\\.");
        if (split.length != 4) {
            throw new IllegalArgumentException("invalid ipv4 string format: " + ipAddress);
        }
        Uint8[] uint8s = new Uint8[4];
        for (int i = 0; i < 4; i++) {
            uint8s[i] = Uint8.valueOf(Integer.parseInt(split[i]));
        }
        return uint8s;
    }

    public static String Uint8sToIpv4(Uint8[] uint8s) {
        if (uint8s.length != 4) {
            throw new IllegalArgumentException("byte array with wrong size should not convert to ipv4");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(uint8s[i].intValue());
            if (i != 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public static Uint8[] ipv6ToUint8s(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        String[] split = ipAddress.split(":");
        if (split.length != 8) {
            throw new IllegalArgumentException("invalid ipv6 string format: " + ipAddress);
        }
        Uint8[] uint8s = new Uint8[16];
        for (int i = 0; i < 8; i++) {
            int num = Integer.parseInt(split[i], 16);
            uint8s[i * 2] = Uint8.valueOf((num & 0xffff) >>> 8);
            uint8s[i * 2 + 1] = Uint8.valueOf((num & 0xff));
        }
        return uint8s;
    }

    public static String Uint8sToIpv6(Uint8[] uint8s) {
        if (uint8s.length != 16) {
            throw new IllegalArgumentException("byte array with wrong size should not convert to ipv6");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(uint8s[i * 2].toString());
            sb.append(uint8s[i * 2 + 1].toString());
            if (i != 7) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static Uint16[] ipv6ToUint16s(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        String[] split = ipAddress.split(":");
        if (split.length != 8) {
            throw new IllegalArgumentException("invalid ipv6 string format: " + ipAddress);
        }
        Uint16[] uint16s = new Uint16[8];
        for (int i = 0; i < 8; i++) {
            uint16s[i] = Uint16.valueOf(Integer.parseInt(split[i], 16));
        }
        return uint16s;
    }

    public static String Uint16sToIpv6(Uint16[] uint16s) {
        if (uint16s.length != 8) {
            throw new IllegalArgumentException("byte array with wrong size should not convert to ipv6");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(uint16s[i]);
            if (i != 7) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
