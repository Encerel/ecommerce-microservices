package by.innowise.inventoryservice.exception;

public class InventoryNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Inventory with id %d not found";

    public InventoryNotFoundException(Integer id) {
        super(String.format(MESSAGE, id));
    }
}
