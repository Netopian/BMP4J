package netopian.network.protocol.bmp4j.transmit;

import org.apache.kafka.common.serialization.Serializer;

import com.google.protobuf.GeneratedMessageV3;


public class Pb2BytesSerializer implements Serializer<GeneratedMessageV3> {

    @Override
    public byte[] serialize(String s, GeneratedMessageV3 generatedMessageV3) {
        return generatedMessageV3.toByteArray();
    }
}
