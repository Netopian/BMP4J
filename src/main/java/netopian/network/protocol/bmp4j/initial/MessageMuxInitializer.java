package netopian.network.protocol.bmp4j.initial;

import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

import netopian.network.protocol.bmp4j.config.KafkaTopicExtConfig;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class MessageMuxInitializer {

    private final KafkaTopicExtConfig kafkaTopicExtConfig;

    private final GenericWebApplicationContext context;

    public MessageMuxInitializer(KafkaTopicExtConfig kafkaTopicExtConfig, GenericWebApplicationContext context) {
        this.kafkaTopicExtConfig = kafkaTopicExtConfig;
        this.context = context;
    }

    @PostConstruct
    private void initializeBeans() {
        if (Objects.isNull(kafkaTopicExtConfig)) {
            return;
        }

        Map<String, KafkaTopicExtConfig.Multiplexer> msg2Topics = kafkaTopicExtConfig.toMuxers();
        log.info("message mux initialized with messages: {}", msg2Topics.size());

        msg2Topics.entrySet()
            .forEach(msg -> context.registerBean(msg.getKey(), KafkaTopicExtConfig.Multiplexer.class, msg.getValue()));
    }

}
