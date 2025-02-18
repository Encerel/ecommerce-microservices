package by.innowise.inventoryservice.config;

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
            @Value("${microservices.api.product:http://localhost:54321}") String productBaseUrl
    ) {
        return WebClient.builder().baseUrl(productBaseUrl).build();
    }

}
