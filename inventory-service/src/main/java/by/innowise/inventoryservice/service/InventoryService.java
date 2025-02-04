package by.innowise.inventoryservice.service;


import by.innowise.inventoryservice.model.api.Product;
import by.innowise.inventoryservice.model.api.ProductQuantity;
import by.innowise.inventoryservice.web.payload.ServerResponse;

import java.util.List;

public interface InventoryService {

    List<Product> takeProductsFromInventory(List<ProductQuantity> products);

    ServerResponse returnProductsToInventory(List<ProductQuantity> products);
}
