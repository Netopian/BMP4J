package netopian.network.protocol.bmp4j.transmit;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;


public interface Transmitter {

    void transmit(Collection<GeneratedMessageV3> messages, BmpMessage.Identities id);
}
