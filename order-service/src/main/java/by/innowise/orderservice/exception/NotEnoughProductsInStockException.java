package by.innowise.orderservice.exception;

import java.util.List;

public class NotEnoughProductsInStockException extends RuntimeException {

    private static final String MESSAGE = "Not enough products in stock ";

    public NotEnoughProductsInStockException(List<Integer> missingProducts) {
        super(MESSAGE + missingProducts);
    }
}
