package by.innowise.inventoryservice.exception;

import by.innowise.inventoryservice.model.api.OutStockProduct;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductOutStockException extends RuntimeException {

    private final List<OutStockProduct> outStockProducts;

    public ProductOutStockException(List<OutStockProduct> products) {
        super();
        outStockProducts = products;
    }


}
