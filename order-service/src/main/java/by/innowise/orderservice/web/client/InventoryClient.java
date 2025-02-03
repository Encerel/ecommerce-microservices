package by.innowise.orderservice.web.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryClient {

    @Value("${microservices.api.inventory.checkForStock}")
    private String checkInventoryUri;
    private final WebClient.Builder clientBuilder;

    public Map<Integer, Boolean> checkInventory(List<Integer> productsIds) {
        return clientBuilder.build()
                .post()
                .uri(checkInventoryUri)
                .bodyValue(productsIds)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
