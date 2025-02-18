package by.innowise.orderservice.exception;

import by.innowise.orderservice.model.entity.OrderStatus;

public class InvalidOrderStatusUpdateException extends RuntimeException {

    private static final String MESSAGE = "Order with id %d already has status %s";

    public InvalidOrderStatusUpdateException(Integer id, OrderStatus status) {
        super(String.format(MESSAGE, id, status));
    }
}
