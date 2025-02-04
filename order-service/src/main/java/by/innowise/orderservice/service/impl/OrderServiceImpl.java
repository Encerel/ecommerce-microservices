package by.innowise.orderservice.service.impl;

import by.innowise.orderservice.exception.OrderAlreadyCanceledException;
import by.innowise.orderservice.exception.OrderNotFoundException;
import by.innowise.orderservice.exception.ProductNotFoundException;
import by.innowise.orderservice.mapper.OrderItemCreateMapper;
import by.innowise.orderservice.mapper.OrderReadMapper;
import by.innowise.orderservice.model.api.Product;
import by.innowise.orderservice.model.api.ProductQuantity;
import by.innowise.orderservice.model.api.ProductsBatch;
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
import by.innowise.orderservice.web.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.joining;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemCreateMapper orderItemCreateMapper;
    private final OrderReadMapper orderReadMapper;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;

    @Override
    @Transactional
    public OrderReadDto placeOrder(OrderCreateDto order) {

        log.info("Placing order for user with id {}", order.getUserId());

        log.debug("Getting ids of products");
        List<OrderItemCreateDto> productsQuantity = order.getItems().stream()
                .map(item -> OrderItemCreateDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build()
                )
                .toList();

        log.debug("Trying to check availability of products in stock to put them in order");
        List<Product> productResponse = inventoryClient.takeProductsFromInventory(productsQuantity);
        Order orderForSave = Order.builder()
                .orderDate(LocalDate.now())
                .userId(order.getUserId())
                .status(OrderStatus.PENDING)
                .build();

        Order preparedOrder = orderRepository.saveAndFlush(orderForSave);
        log.info("Order with id {} was created for user with id {}", preparedOrder.getId(), order.getUserId());
        List<OrderItem> orderItems = orderItemCreateMapper.toListEntity(order.getItems(), preparedOrder);
        preparedOrder.addItems(orderItems);
        Order savedOrder = orderRepository.saveAndFlush(preparedOrder);
        return orderReadMapper.toDto(savedOrder, productResponse);
    }

    @Override
    public OrderReadDto getOrderById(Integer orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );

        List<Integer> productsIds = findProductsIds(order.getItems());
        List<Product> productsByIds = findProductsByIds(productsIds);

        return orderReadMapper.toDto(order, productsByIds);
    }

    @Override
    public List<OrderReadDto> getOrdersByUserId(UUID userId) {
        return List.of();
    }

    @Override
    @Transactional
    public OrderReadDto updateOrderStatus(Integer orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );

        List<Integer> productsIds = findProductsIds(order.getItems());
        List<Product> productsByIds = findProductsByIds(productsIds);

        order.setStatus(status);
        return orderReadMapper.toDto(orderRepository.save(order), productsByIds);
    }

    @Override
    @Transactional
    public OrderReadDto cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderAlreadyCanceledException(orderId);
        }
        inventoryClient.returnProductsToInventory(map(order.getItems()));
        return updateOrderStatus(orderId, OrderStatus.CANCELED);
    }

    @Override
    public List<OrderReadDto> getAllOrders() {
        return List.of();
    }

    @Override
    public boolean checkProductAvailability(List<OrderItemReadDto> items) {
        return false;
    }

    private List<Integer> findProductsIds(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getProductId)
                .toList();
    }

    private List<Product> findProductsByIds(List<Integer> productsIds) {
        ProductsBatch batchResponse = productClient.findByIds(productsIds);
        if (!batchResponse.getErrors().isEmpty()) {
            String errorMessage = batchResponse.getErrors().stream()
                    .map(error -> error.getMessage() + "\n")
                    .collect(joining());
            throw new ProductNotFoundException(errorMessage);
        }
        return batchResponse.getProducts();
    }

    private List<ProductQuantity> map(List<OrderItem> orderItems) {
        List<ProductQuantity> result = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            ProductQuantity request = ProductQuantity.builder()
                    .productId(orderItem.getProductId())
                    .quantity(orderItem.getQuantity())
                    .build();
            result.add(request);
        }
        return result;
    }



}
