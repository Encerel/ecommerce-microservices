package by.innowise.orderservice.service.impl;

import by.innowise.orderservice.exception.NotEnoughProductsInStockException;
import by.innowise.orderservice.mapper.OrderItemCreateMapper;
import by.innowise.orderservice.mapper.OrderReadMapper;
import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderItemCreateDto;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.dto.OrderReadDto;
import by.innowise.orderservice.model.entity.Order;
import by.innowise.orderservice.model.entity.OrderItem;
import by.innowise.orderservice.model.entity.OrderStatus;
import by.innowise.orderservice.repository.OrderRepository;
import by.innowise.orderservice.service.OrderService;
import by.innowise.orderservice.web.client.InventoryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemCreateMapper orderItemCreateMapper;
    private final OrderReadMapper orderReadMapper;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public OrderReadDto placeOrder(OrderCreateDto order) {

        log.info("Placing order for user with id {}", order.getUserId());

        log.debug("Getting ids of products");
        List<Integer> productIds = order.getItems().stream()
                .map(OrderItemCreateDto::getProductId)
                .toList();
        log.debug("Trying to check availability of products in stock");
        Map<Integer, Boolean> inventoryStatus = inventoryClient.checkInventory(productIds);
        List<Integer> missingProducts = inventoryStatus.entrySet().stream()
                .filter(item -> !item.getValue())
                .map(Map.Entry::getKey)
                .toList();

        if (!missingProducts.isEmpty()) {
            log.warn("Not enough products in stock {}", missingProducts);
            throw new NotEnoughProductsInStockException(missingProducts);
        }

        Order orderForSave = Order.builder()
                .orderDate(LocalDate.now())
                .userId(order.getUserId())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.saveAndFlush(orderForSave);
        log.info("Order with id {} was created for user with id {}", savedOrder.getId(), order.getUserId());
        List<OrderItem> orderItems = orderItemCreateMapper.toListEntity(order.getItems(), savedOrder);
        savedOrder.addItems(orderItems);
        return orderReadMapper.toDto(savedOrder);
    }

    @Override
    public OrderReadDto getOrderById(Integer orderId) {
        return null;
    }

    @Override
    public List<OrderReadDto> getOrdersByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public OrderReadDto updateOrderStatus(Integer orderId, OrderStatus status) {
        return null;
    }

    @Override
    public OrderReadDto cancelOrder(Integer orderId) {
        return null;
    }

    @Override
    public List<OrderReadDto> getAllOrders() {
        return List.of();
    }

    @Override
    public boolean checkProductAvailability(List<OrderItemReadDto> items) {
        return false;
    }
}
