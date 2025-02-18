package by.innowise.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientConfig {

    @Bean
    @Primary
    public WebClient inventoryWebClient(
            @Value("${microservices.api.inventory:http://localhost:54321}") String inventoryBaseUrl
    ) {
        return WebClient.builder().baseUrl(inventoryBaseUrl).build();
    }

}
