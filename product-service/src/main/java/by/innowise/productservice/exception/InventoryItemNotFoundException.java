package by.innowise.productservice.exception;

public class InventoryItemNotFoundException extends RuntimeException {

    public InventoryItemNotFoundException(String message) {
        super(message);
    }
}
