package by.innowise.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Order with id %d not found";

    public OrderNotFoundException(Integer id) {
        super(String.format(MESSAGE, id));
    }
}
