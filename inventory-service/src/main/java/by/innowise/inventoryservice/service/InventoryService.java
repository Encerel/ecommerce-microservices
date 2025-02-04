package by.innowise.inventoryservice.service;


import by.innowise.inventoryservice.model.api.Product;
import by.innowise.inventoryservice.model.api.ProductQuantity;

import java.util.List;

public interface InventoryService {

    List<Product> takeProductsFromInventory(List<ProductQuantity> products);

}
