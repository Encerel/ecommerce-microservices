package by.innowise.inventoryservice.model.api;

import lombok.Value;

@Value
public class OutStockProduct {

    Integer productId;
    Integer availableQuantity;
    Integer requiredQuantity;

}
