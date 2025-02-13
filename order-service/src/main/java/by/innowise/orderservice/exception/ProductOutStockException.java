package by.innowise.orderservice.exception;

import by.innowise.orderservice.model.api.OutStockProduct;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductOutStockException extends RuntimeException {

    private static final String MESSAGE = "Some products are out of stock: ";
    private final List<OutStockProduct> outStockProducts;

    public ProductOutStockException(List<OutStockProduct> products) {
        super(MESSAGE + products);
        outStockProducts = products;
    }


}
