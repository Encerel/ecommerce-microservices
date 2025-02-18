package by.innowise.orderservice.service;


import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderDetailsDto;
import by.innowise.orderservice.model.dto.OrderSummaryDto;
import by.innowise.orderservice.model.entity.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderDetailsDto placeOrder(OrderCreateDto order);

    OrderDetailsDto findById(Integer orderId);

    List<OrderSummaryDto> findAllByUserId(UUID userId);

    OrderSummaryDto updateOrderStatus(Integer orderId, OrderStatus status);

    OrderSummaryDto cancelOrder(Integer orderId);

    Page<OrderSummaryDto> findAll(int offset, int pageSize);

}
