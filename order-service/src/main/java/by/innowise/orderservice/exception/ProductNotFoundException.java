package by.innowise.orderservice.exception;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Product with id %d not found";

    public ProductNotFoundException(Integer id) {
        super(String.format(MESSAGE, id));
    }
}
