package netopian.network.protocol.bmp4j.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Setter
@Getter
@ToString
public class KafkaTopicExtConfig {

    private List<Topic> topics;

    @Setter
    @Getter
    @ToString
    public static final class Topic {

        String name;

        Integer numPartitions = 3;

        Short replicationFactor = 1;

        boolean withKey = true;

        List<String> messageTypes;

        public NewTopic toNewTopic() {
            return new NewTopic(name, numPartitions, replicationFactor);
        }
    }

    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    public static final class Multiplexer {

        boolean withKey;

        List<String> topicNames;
    }

    public Map<String, Multiplexer> toMuxers() {
        Objects.requireNonNull(topics);
        Map<String, Multiplexer> temp = new ConcurrentHashMap<>();
        topics.forEach(topic -> topic.getMessageTypes().forEach(type -> {
            temp.putIfAbsent(type, new Multiplexer(topic.withKey, new ArrayList()));
            temp.get(type).getTopicNames().add(topic.getName());
        }));
        return temp;
    }

}
