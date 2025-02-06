package by.innowise.inventoryservice.web.controller;

import by.innowise.inventoryservice.model.api.OrderItem;
import by.innowise.inventoryservice.model.api.ProductQuantity;
import by.innowise.inventoryservice.model.api.ProductStock;
import by.innowise.inventoryservice.service.InventoryService;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/take")
    public List<OrderItem> takeProductsFromInventory(@RequestBody List<ProductQuantity> products) {
        return inventoryService.takeProductsFromInventory(products);
    }

    @PostMapping("/return")
    public ServerResponse returnProductsToInventory(@RequestBody @Valid List<ProductQuantity> products) {
        return inventoryService.returnProductsToInventory(products);
    }

    @PostMapping
    public ServerResponse addNewProductToInventory(@RequestBody @Valid ProductQuantity item) {
        return inventoryService.addNewProductInInventory(item);
    }

    @PatchMapping
    public ProductStock increaseProductStock(@RequestBody @Valid ProductQuantity item) {
        return inventoryService.increaseProductStock(item);
    }


    @DeleteMapping("/{productId}")
    public ServerResponse deleteProductFromInventory(@PathVariable Integer productId) {
        return inventoryService.deleteByProductId(productId);
    }
}
