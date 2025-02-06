package by.innowise.inventoryservice.web.client;

import by.innowise.inventoryservice.exception.ErrorParsingResponseException;
import by.innowise.inventoryservice.exception.ProductNotFoundException;
import by.innowise.inventoryservice.exception.UnknownResponseException;
import by.innowise.inventoryservice.model.api.ProductsBatch;
import by.innowise.inventoryservice.model.entity.ProductStatus;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import by.innowise.inventoryservice.web.payload.request.ProductStatusRequest;
import by.innowise.inventoryservice.web.payload.response.AdviceErrorMessage;
import by.innowise.inventoryservice.web.payload.response.MessageServerResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static by.innowise.inventoryservice.constant.ErrorMessage.ERROR_PARSING_RESPONSE;
import static by.innowise.inventoryservice.constant.ErrorMessage.UNKNOWN_RESPONSE_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    @Value("${microservices.api.product}")
    private String productServiceUrl;

    public ServerResponse updateProductStatus(Integer productId, ProductStatus newStatus) {
        return WebClient.create(productServiceUrl)
                .patch()
                .uri("/{productId}/status", productId)
                .bodyValue(new ProductStatusRequest(newStatus))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .map(this::parseServerResponse)
                .block();
    }

    public ProductsBatch getProductsByIds(List<Integer> productIds) {
        return WebClient.create(productServiceUrl)
                .post()
                .uri("/batch")
                .bodyValue(productIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ProductsBatch>() {
                })
                .block();
    }

    private Mono<? extends RuntimeException> handleErrorResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode root = objectMapper.readTree(body);

                        if (root.has("message")) {
                            return handleAdviceErrorResponse(objectMapper, root);
                        } else {
                            return Mono.error(new UnknownResponseException(UNKNOWN_RESPONSE_TYPE));
                        }
                    } catch (Exception e) {
                        return Mono.error(new ErrorParsingResponseException(ERROR_PARSING_RESPONSE));
                    }
                });
    }

    private Mono<? extends RuntimeException> handleAdviceErrorResponse(ObjectMapper objectMapper, JsonNode root) {
        try {
            AdviceErrorMessage errorResponse = objectMapper.treeToValue(root, AdviceErrorMessage.class);
            return Mono.error(new ProductNotFoundException(errorResponse.getMessage()));
        } catch (Exception e) {
            return Mono.error(new ErrorParsingResponseException(ERROR_PARSING_RESPONSE));
        }
    }

    private ServerResponse parseServerResponse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);

            if (root.isObject()) {
                return objectMapper.readValue(json, MessageServerResponse.class);
            } else {
                log.warn("Unknown response type");
                throw new UnknownResponseException(UNKNOWN_RESPONSE_TYPE);
            }
        } catch (Exception e) {
            log.error("Error parsing response", e);
            throw new ErrorParsingResponseException(ERROR_PARSING_RESPONSE);
        }
    }
}
