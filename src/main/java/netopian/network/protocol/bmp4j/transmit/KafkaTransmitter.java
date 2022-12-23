package netopian.network.protocol.bmp4j.transmit;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import netopian.network.protocol.bmp4j.model.BmpMessage;
import netopian.network.protocol.common.sealing.TypelessV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

import netopian.network.protocol.bmp4j.config.BmpServerExtConfig;
import netopian.network.protocol.bmp4j.config.KafkaTopicExtConfig;


@Component
public class KafkaTransmitter implements Transmitter {

    @Autowired
    private KafkaTopicExtConfig kafkaTopicExtConfig;

    @Autowired
    private BmpServerExtConfig bmpServerExtConfig;

    @Autowired
    private KafkaTemplate<String, GeneratedMessageV3> kafkaTemplate;

    private Map<String, KafkaTopicExtConfig.Multiplexer> topics;

    private long modelVersion;

    private AtomicLong seqNumber;

    @PostConstruct
    private void initializeBeans() {
        topics = kafkaTopicExtConfig.toMuxers();
        modelVersion =
            (bmpServerExtConfig.getModel() == null ? new BmpServerExtConfig.Model() : bmpServerExtConfig.getModel())
                .toFullVersion();
        seqNumber = new AtomicLong(((long) bmpServerExtConfig.getSeqPrefix() & (long) 0xff) << 48);
    }

    @Override
    public void transmit(Collection<GeneratedMessageV3> messages, BmpMessage.Identities id) {

        messages.stream().forEach((message) -> {
            KafkaTopicExtConfig.Multiplexer downStreams = topics.get(id.getMessageName());

            if (downStreams != null && downStreams.getTopicNames() != null) {
                downStreams.getTopicNames()
                    .forEach((topic) -> kafkaTemplate.send(topic,
                        downStreams.isWithKey() ? id.getRemoteAddress() : null, sealTypelessV3(message)));
            }
        });

    }

    private GeneratedMessageV3 sealTypelessV3(GeneratedMessageV3 content) {
        return TypelessV3.RecordAny.newBuilder()
            .addTypelessRequest(Any.pack(content))
            .setVersion(modelVersion)
            .setSequence(seqNumber.incrementAndGet())
            .setCharsetValue(0) // utf-8 is mandatory
            .build();
    }
}
