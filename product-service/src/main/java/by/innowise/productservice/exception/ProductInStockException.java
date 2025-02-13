package by.innowise.productservice.exception;

public class ProductInStockException extends RuntimeException {

    private static final String MESSAGE = "Product with id %d is in stock. Deleting is not possible!";

    public ProductInStockException(Integer productId) {
        super(String.format(MESSAGE, productId));
    }
}
