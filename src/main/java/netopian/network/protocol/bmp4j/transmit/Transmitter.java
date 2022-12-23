package netopian.network.protocol.bmp4j.transmit;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;
import netopian.network.protocol.bmp4j.model.BmpMessage;


public interface Transmitter {

    void transmit(Collection<GeneratedMessageV3> messages, BmpMessage.Identities id);
}
