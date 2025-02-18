package by.innowise.inventoryservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaConfig {
    @Bean
    public KafkaTemplate kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
