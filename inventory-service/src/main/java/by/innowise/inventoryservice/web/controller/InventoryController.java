package by.innowise.inventoryservice.web.controller;

import by.innowise.inventoryservice.model.api.Product;
import by.innowise.inventoryservice.model.api.ProductQuantity;
import by.innowise.inventoryservice.service.InventoryService;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/take")
    public List<Product> takeProductsFromInventory(@RequestBody List<ProductQuantity> products) {
        return inventoryService.takeProductsFromInventory(products);
    }

    @PostMapping("/return")
    public ServerResponse returnProductsToInventory(@RequestBody List<ProductQuantity> products) {
        return inventoryService.returnProductsToInventory(products);
    }
}
