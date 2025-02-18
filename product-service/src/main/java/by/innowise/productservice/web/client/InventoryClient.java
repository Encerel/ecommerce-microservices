package by.innowise.productservice.web.client;

import by.innowise.productservice.exception.ErrorParsingResponseException;
import by.innowise.productservice.exception.InventoryItemNotFoundException;
import by.innowise.productservice.exception.UnknownResponseException;
import by.innowise.productservice.model.api.ProductQuantity;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import by.innowise.productservice.web.payload.response.MessageServerResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static by.innowise.productservice.constant.ErrorMessage.ERROR_PARSING_RESPONSE;
import static by.innowise.productservice.constant.ErrorMessage.UNKNOWN_RESPONSE_TYPE;


@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

    private final WebClient inventoryWebClient;

    public ServerResponse addNewProductInInventory(ProductQuantity item) {
        log.info("Add new product in inventory");
        return inventoryWebClient
                .post()
                .uri("/api/inventories/items")
                .bodyValue(item)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .map(this::parseServerResponse)
                .block();

    }


    public ServerResponse deleteProductFromInventory(Integer productId) {
        log.info("Try to delete product with {} from inventory", productId);
        return inventoryWebClient
                .delete()
                .uri("/api/inventories/items/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .map(this::parseServerResponse)
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
            return Mono.error(new InventoryItemNotFoundException(errorResponse.getMessage()));
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

