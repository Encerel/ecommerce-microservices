package by.innowise.inventoryservice.web.controller;

import by.innowise.inventoryservice.model.api.OrderItem;
import by.innowise.inventoryservice.model.api.ProductQuantityChange;
import by.innowise.inventoryservice.model.api.ProductStock;
import by.innowise.inventoryservice.model.api.TakenProductQuantity;
import by.innowise.inventoryservice.service.InventoryService;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/items/take")
    public List<OrderItem> takeProductsFromInventory(@RequestBody @Valid List<TakenProductQuantity> products) {
        return inventoryService.takeProductsFromInventory(products);
    }

    @PostMapping("/items/return")
    public ResponseEntity<ServerResponse> returnProductsToInventory(@RequestBody @Valid List<ProductQuantityChange> products) {
        return ResponseEntity.ok(inventoryService.returnProductsToInventory(products));
    }

    @PostMapping("/items")
    public ResponseEntity<ServerResponse> addNewProductToInventory(@RequestBody @Valid ProductQuantityChange item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addNewProductInInventory(item));
    }

    @PatchMapping("/items")
    public ProductStock increaseProductStock(@RequestBody @Valid ProductQuantityChange item) {
        return inventoryService.increaseProductStock(item);
    }

    @DeleteMapping("/items/{productId}")
    public ServerResponse deleteProductFromInventory(@PathVariable Integer productId) {
        return inventoryService.deleteByProductId(productId);
    }
}
