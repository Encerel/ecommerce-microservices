package by.innowise.inventoryservice.service;


import by.innowise.inventoryservice.model.api.OrderItem;
import by.innowise.inventoryservice.model.api.ProductQuantityChange;
import by.innowise.inventoryservice.model.api.ProductStock;
import by.innowise.inventoryservice.model.api.TakenProductQuantity;
import by.innowise.inventoryservice.web.payload.ServerResponse;

import java.util.List;

public interface InventoryService {

    List<OrderItem> takeProductsFromInventory(List<TakenProductQuantity> products);

    ServerResponse returnProductsToInventory(List<ProductQuantityChange> products);

    ServerResponse addNewProductInInventory(ProductQuantityChange productQuantityChange);

    ProductStock increaseProductStock(ProductQuantityChange productQuantityChange);

    ServerResponse deleteByProductId(Integer productId);
}
