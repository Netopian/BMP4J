package netopian.network.protocol.bmp4j.initial;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;

import netopian.network.protocol.bmp4j.config.KafkaTopicExtConfig;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class KafkaTopicInitializer {

    private final KafkaTopicExtConfig kafkaTopicExtConfig;

    private final GenericWebApplicationContext context;

    public KafkaTopicInitializer(KafkaTopicExtConfig kafkaTopicExtConfig, GenericWebApplicationContext context) {
        this.kafkaTopicExtConfig = kafkaTopicExtConfig;
        this.context = context;
    }

    @PostConstruct
    private void initializeBeans() {
        if (Objects.isNull(kafkaTopicExtConfig)) {
            return;
        }
        kafkaTopicExtConfig.getTopics().forEach(t -> context.registerBean(t.getName(), NewTopic.class, t::toNewTopic));
        log.info("kafka producer initialized with topics: {}", kafkaTopicExtConfig.getTopics().size());

    }
}
