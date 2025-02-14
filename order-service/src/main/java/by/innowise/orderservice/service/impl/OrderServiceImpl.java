package by.innowise.orderservice.service.impl;

import by.innowise.orderservice.constant.TopicName;
import by.innowise.orderservice.exception.InvalidOrderStatusUpdateException;
import by.innowise.orderservice.exception.OrderAlreadyCanceledException;
import by.innowise.orderservice.exception.OrderNotFoundException;
import by.innowise.orderservice.mapper.OrderDetailsMapper;
import by.innowise.orderservice.mapper.OrderItemReadMapper;
import by.innowise.orderservice.mapper.OrderSummaryMapper;
import by.innowise.orderservice.model.api.ProductQuantity;
import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderDetailsDto;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.dto.OrderSummaryDto;
import by.innowise.orderservice.model.entity.Order;
import by.innowise.orderservice.model.entity.OrderItem;
import by.innowise.orderservice.model.entity.OrderStatus;
import by.innowise.orderservice.repository.OrderRepository;
import by.innowise.orderservice.service.OrderService;
import by.innowise.orderservice.web.client.InventoryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.innowise.orderservice.constant.TopicName.ORDER_STATUS_UPDATES_EVENTS_TOPIC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsMapper orderDetailsMapper;
    private final OrderSummaryMapper orderSummaryMapper;
    private final InventoryClient inventoryClient;
    private final OrderItemReadMapper orderItemReadMapper;
    private final KafkaTemplate<String, OrderSummaryDto> orderKafkaTemplate;

    @Override
    @Transactional
    public OrderDetailsDto placeOrder(OrderCreateDto orderCreateDto) {

        log.info("Placing order for user with id {}", orderCreateDto.getUserId());

        log.debug("Getting ids of products");
        List<ProductQuantity> productsQuantity = orderCreateDto.getItems().stream()
                .map(item -> ProductQuantity.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build()
                )
                .toList();

        log.info("Trying to check availability of products in stock to put them in order");
        List<OrderItemReadDto> foundInventoryItems = inventoryClient.takeProductsFromInventory(productsQuantity);
        log.info("Product from inventory was taken successfully!");
        Order orderForSave = Order.builder()
                .orderDate(LocalDateTime.now())
                .userId(orderCreateDto.getUserId())
                .userEmail(orderCreateDto.getUserEmail())
                .status(OrderStatus.PENDING)
                .build();

        Order preparedOrder = orderRepository.save(orderForSave);
        log.debug("Send email message about order creation with. Order: {}", preparedOrder);
        orderKafkaTemplate.send(TopicName.ORDER_CREATE_EVENTS_TOPIC,
                String.valueOf(preparedOrder.getId()),
                orderSummaryMapper.toDto(preparedOrder)
        );

        log.info("Order with id {} was created for user with id {}", preparedOrder.getId(), orderCreateDto.getUserId());
        List<OrderItem> orderItems = orderItemReadMapper.toListEntity(foundInventoryItems);
        preparedOrder.addItems(orderItems);
        Order savedOrder = orderRepository.saveAndFlush(preparedOrder);
        log.info("Order with id {} was placed for user with id {}", savedOrder.getId(), orderCreateDto.getUserId());
        return orderDetailsMapper.toDto(savedOrder);
    }

    @Override
    public OrderDetailsDto findById(Integer orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.warn("Order with id {} not found trying to find order by id!", orderId);
                    return new OrderNotFoundException(orderId);
                }
        );
        return orderDetailsMapper.toDto(order);
    }

    @Override
    public List<OrderSummaryDto> findAllByUserId(UUID userId) {
        log.info("Try to get orders by user with id {}", userId);
        List<Order> userOrders = orderRepository.findAllByUserId(userId);
        return orderSummaryMapper.toListDto(userOrders);
    }

    @Override
    @Transactional
    public OrderDetailsDto updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.warn("Order with id {} not found trying to update order status!", orderId);
                    return new OrderNotFoundException(orderId);
                }
        );
        log.info("Try to update order status from {} to {} for order with id {}", order.getStatus(), status, orderId);
        if (order.getStatus() == status) {
            log.warn("Statuses have the same value!");
            throw new InvalidOrderStatusUpdateException(order.getId(), status);
        }
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        log.info("OrderStatus was updated for order with id {}", savedOrder.getId());
        log.debug("Send email message about order status changing. Order: {}", savedOrder);
        orderKafkaTemplate.send(ORDER_STATUS_UPDATES_EVENTS_TOPIC,
                orderSummaryMapper.toDto(savedOrder));
        return orderDetailsMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDetailsDto cancelOrder(Integer orderId) {
        log.info("Try to cancel order with id {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.warn("Order with id {} not found trying to cancel order!", orderId);
                    return new OrderNotFoundException(orderId);
                }
        );
        if (order.getStatus() == OrderStatus.CANCELED) {
            log.warn("Order with id {} has already canceled!", orderId);
            throw new OrderAlreadyCanceledException(orderId);
        }
        log.info("Try to return product in inventory");
        inventoryClient.returnProductsToInventory(map(order.getItems()));
        log.info("Products was returned successfully");
        return updateOrderStatus(orderId, OrderStatus.CANCELED);
    }

    @Override
    public Page<OrderSummaryDto> findAll(int offset, int pageSize) {
        log.info("Search for all products");
        return orderRepository.findAll(PageRequest.of(offset, pageSize)).map(orderSummaryMapper::toDto);
    }


    private List<ProductQuantity> map(List<OrderItem> orderItems) {
        List<ProductQuantity> result = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            ProductQuantity request = ProductQuantity.builder()
                    .inventoryId(orderItem.getInventoryId())
                    .productId(orderItem.getProductId())
                    .quantity(orderItem.getQuantity())
                    .build();
            result.add(request);
        }
        return result;
    }


}
