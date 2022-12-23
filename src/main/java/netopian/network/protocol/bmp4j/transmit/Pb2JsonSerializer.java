package netopian.network.protocol.bmp4j.transmit;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.common.serialization.Serializer;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.util.JsonFormat;

import gobgpapi.Attribute;
import lombok.SneakyThrows;


public class Pb2JsonSerializer implements Serializer<GeneratedMessageV3> {
    private static final JsonFormat.Printer printer;

    static {
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder()
            .add(BmpMessage.getDescriptor().getMessageTypes())
            .add(Attribute.getDescriptor().getMessageTypes())
            .add(Capability.getDescriptor().getMessageTypes())
            .build();
        printer = JsonFormat.printer().usingTypeRegistry(typeRegistry).omittingInsignificantWhitespace();
    }

    @SneakyThrows
    @Override
    public byte[] serialize(String s, GeneratedMessageV3 generatedMessageV3) {
        return printer.print(generatedMessageV3).getBytes(StandardCharsets.UTF_8);
    }
}
