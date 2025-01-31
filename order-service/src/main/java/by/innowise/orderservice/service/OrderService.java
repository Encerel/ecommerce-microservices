package by.innowise.orderservice.service;


import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.dto.OrderReadDto;
import by.innowise.orderservice.model.entity.order.OrderStatus;
import by.innowise.orderservice.web.payload.ServerResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ResponseEntity<ServerResponse> placeOrder(OrderCreateDto order);

    OrderReadDto getOrderById(Integer orderId);

    List<OrderReadDto> getOrdersByUserId(UUID userId);

    OrderReadDto updateOrderStatus(Integer orderId, OrderStatus status);

    OrderReadDto cancelOrder(Integer orderId);

    List<OrderReadDto> getAllOrders();

    boolean checkProductAvailability(List<OrderItemReadDto> items);

}
