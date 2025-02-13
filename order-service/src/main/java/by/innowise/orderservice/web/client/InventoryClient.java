package by.innowise.orderservice.web.client;

import by.innowise.orderservice.exception.ErrorParsingResponseException;
import by.innowise.orderservice.exception.ProductNotFoundException;
import by.innowise.orderservice.exception.ProductOutStockException;
import by.innowise.orderservice.exception.UnknownResponseException;
import by.innowise.orderservice.model.api.ProductQuantity;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.web.payload.ServerResponse;
import by.innowise.orderservice.web.payload.response.AdviceErrorMessage;
import by.innowise.orderservice.web.payload.response.MessageServerResponse;
import by.innowise.orderservice.web.payload.response.OutStockProductResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static by.innowise.orderservice.constant.ErrorMessage.ERROR_PARSING_RESPONSE;
import static by.innowise.orderservice.constant.ErrorMessage.UNKNOWN_RESPONSE_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public List<OrderItemReadDto> takeProductsFromInventory(List<ProductQuantity> products) {
        log.info("Taking products {} from inventory", products);

        return inventoryWebClient
                .post()
                .uri("/api/inventories/items/take")
                .bodyValue(products)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .map(this::parseProductResponse)
                .block();
    }

    public ServerResponse returnProductsToInventory(List<ProductQuantity> products) {
        log.info("Returning products {} in inventory", products);
        return inventoryWebClient
                .post()
                .uri("/api/inventories/items/return")
                .bodyValue(products)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .map(this::parseMessageServerResponse)
                .block();
    }

    private Mono<? extends RuntimeException> handleErrorResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode root = objectMapper.readTree(body);

                        if (root.has("outStockProducts")) {
                            return handleOutOfStockResponse(objectMapper, root);
                        } else if (root.has("message")) {
                            return handleAdviceErrorResponse(objectMapper, root);
                        } else {
                            return Mono.error(new UnknownResponseException(UNKNOWN_RESPONSE_TYPE));
                        }
                    } catch (Exception e) {
                        return Mono.error(new ErrorParsingResponseException(ERROR_PARSING_RESPONSE));
                    }
                });
    }

    private Mono<? extends RuntimeException> handleOutOfStockResponse(ObjectMapper objectMapper, JsonNode root) {
        try {
            OutStockProductResponse errorResponse = objectMapper.treeToValue(root, OutStockProductResponse.class);
            return Mono.error(new ProductOutStockException(errorResponse.getOutStockProducts()));
        } catch (Exception e) {
            return Mono.error(new ErrorParsingResponseException(ERROR_PARSING_RESPONSE));
        }
    }

    private Mono<? extends RuntimeException> handleAdviceErrorResponse(ObjectMapper objectMapper, JsonNode root) {
        try {
            AdviceErrorMessage errorResponse = objectMapper.treeToValue(root, AdviceErrorMessage.class);
            return Mono.error(new ProductNotFoundException(errorResponse.getMessage()));
        } catch (Exception e) {
            return Mono.error(new ErrorParsingResponseException(ERROR_PARSING_RESPONSE));
        }
    }

    private List<OrderItemReadDto> parseProductResponse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);

            if (root.isArray()) {
                return objectMapper.readValue(json, new TypeReference<>() {
                });
            } else {
                log.warn("Unknown response type");
                throw new UnknownResponseException(UNKNOWN_RESPONSE_TYPE);
            }
        } catch (Exception e) {
            log.error("Error parsing response", e);
            throw new ErrorParsingResponseException(ERROR_PARSING_RESPONSE);
        }
    }

    private MessageServerResponse parseMessageServerResponse(String json) {
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
