package by.innowise.orderservice.exception;

public class OrderAlreadyCanceledException extends RuntimeException {

    public static final String MESSAGE = "Order with id %d is already canceled";

    public OrderAlreadyCanceledException(Integer orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
