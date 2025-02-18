package by.innowise.orderservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientConfig {

    @Bean
    @Qualifier("inventoryWebClient")
    public WebClient inventoryWebClient(
            @Value("${microservices.api.inventory:http://localhost:54321}") String inventoryBaseUrl
    ) {
        return WebClient.builder().baseUrl(inventoryBaseUrl).build();
    }

    @Bean
    @Qualifier("productWebClient")
    public WebClient productWebClient(
            @Value("${microservices.api.product:http://localhost:54322}") String productBaseUrl
    ) {
        return WebClient.builder().baseUrl(productBaseUrl).build();
    }

}
