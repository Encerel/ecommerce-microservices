package by.innowise.inventoryservice.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Product with id %d not found";

    public ProductNotFoundException(Integer productId) {
        super(String.format(MESSAGE, productId));
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

}
