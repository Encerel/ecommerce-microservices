package by.innowise.orderservice.web.client;

import by.innowise.orderservice.model.api.ProductsBatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    @Value("${microservices.api.product.findByIds}")
    private String findProductsBatchUri;

    private final WebClient.Builder clientBuilder;


    public ProductsBatch findByIds(List<Integer> ids) {
        return clientBuilder.build()
                .post()
                .uri(findProductsBatchUri)
                .bodyValue(ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProductsBatch>() {
                })
                .block();
    }
}
