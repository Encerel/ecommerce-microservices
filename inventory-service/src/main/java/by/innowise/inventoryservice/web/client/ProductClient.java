package by.innowise.inventoryservice.web.client;

import by.innowise.inventoryservice.model.api.ProductsBatch;
import by.innowise.inventoryservice.model.entity.ProductStatus;
import by.innowise.inventoryservice.web.payload.request.ProductStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClient {

    @Value("${microservices.api.product.changeStatus}")
    private String changeProductStatusUri;

    @Value("${microservices.api.product.findByIds}")
    private String findProductsBatchUri;

    private final WebClient.Builder webClientBuilder;

    public void updateProductStatus(Integer productId, ProductStatus newStatus) {
        webClientBuilder.build()
                .patch()
                .uri(changeProductStatusUri, productId)
                .bodyValue(new ProductStatusRequest(newStatus))
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
                                })
                )
                .bodyToMono(Void.class)
                .block();
    }

    public ProductsBatch getProductsByIds(List<Integer> productIds) {
        return webClientBuilder.build()
                .post()
                .uri(findProductsBatchUri)
                .bodyValue(productIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProductsBatch>() {
                })
                .block();
    }
}
