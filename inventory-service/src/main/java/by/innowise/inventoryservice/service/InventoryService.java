package by.innowise.inventoryservice.service;


import by.innowise.inventoryservice.model.api.OrderItem;
import by.innowise.inventoryservice.model.api.ProductQuantity;
import by.innowise.inventoryservice.model.api.ProductStock;
import by.innowise.inventoryservice.web.payload.ServerResponse;

import java.util.List;

public interface InventoryService {

    List<OrderItem> takeProductsFromInventory(List<ProductQuantity> products);

    ServerResponse returnProductsToInventory(List<ProductQuantity> products);

    ServerResponse addNewProductInInventory(ProductQuantity productQuantity);

    ProductStock increaseProductStock(ProductQuantity productQuantity);

    ServerResponse deleteByProductId(Integer productId);
}
