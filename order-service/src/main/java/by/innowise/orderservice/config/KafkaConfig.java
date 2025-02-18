package by.innowise.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

import static by.innowise.orderservice.constant.TopicName.ORDER_CREATE_EVENTS_TOPIC;
import static by.innowise.orderservice.constant.TopicName.ORDER_STATUS_UPDATES_EVENTS_TOPIC;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic createOrderCreateEventsTopic() {
        return TopicBuilder.name(ORDER_CREATE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    NewTopic createOrderConfirmEventsTopic() {
        return TopicBuilder.name(ORDER_STATUS_UPDATES_EVENTS_TOPIC)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
