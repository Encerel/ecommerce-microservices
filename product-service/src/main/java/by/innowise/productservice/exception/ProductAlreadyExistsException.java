package by.innowise.productservice.exception;

public class ProductAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "Product with name: %s already exists";

    public ProductAlreadyExistsException(String name) {
        super(String.format(MESSAGE, name));
    }
}
