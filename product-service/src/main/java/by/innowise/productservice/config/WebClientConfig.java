package by.innowise.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${microservices.api.inventory}")
    private String inventoryServiceUrl;


    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder builder) {
        return builder.baseUrl(inventoryServiceUrl)
                .filter(new ServletBearerExchangeFilterFunction())
                .build();
    }

}
