package by.innowise.orderservice.config;

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

    @Value("${microservices.api.product}")
    private String productServiceUrl;

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

    @Bean
    public WebClient productWebClient(WebClient.Builder builder) {
        return builder.baseUrl(productServiceUrl)
                .filter(new ServletBearerExchangeFilterFunction())
                .build();
    }

}
