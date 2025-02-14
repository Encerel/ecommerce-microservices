package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.exception.ProductNotFoundException;
import by.innowise.orderservice.model.api.Product;
import by.innowise.orderservice.model.api.ProductsBatch;
import by.innowise.orderservice.model.dto.OrderDetailsDto;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.entity.Order;
import by.innowise.orderservice.model.entity.OrderItem;
import by.innowise.orderservice.web.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDetailsMapper implements Mapper<Order, OrderDetailsDto> {

    private final OrderItemReadMapper orderItemReadMapper;
    private final ProductClient productClient;

    @Override
    public OrderDetailsDto toDto(Order entity) {
        log.debug("Try to map order entity to OrderDetailsDto");
        if (entity == null) {
            log.warn("Order entity is null!");
            return null;
        }
        log.debug("Getting products ids");
        List<Integer> productsIds = findProductsIds(entity.getItems());
        log.debug("Getting products by products ids");
        List<Product> productsByIds = findProductsByIds(productsIds);
        log.debug("Merge products with order items into list OrderItemReadDto");
        List<OrderItemReadDto> orderItems = orderItemReadMapper.toListDto(entity.getItems(), productsByIds);
        log.debug("Calculate order total price");
        BigDecimal totalPrice = calculateTotalPrice(orderItems);
        log.debug("OrderDetailsDto is ready");
        return OrderDetailsDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userEmail(entity.getUserEmail())
                .items(orderItems)
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public Order toEntity(OrderDetailsDto dto) {
        if (dto == null) {
            log.warn("Order dto is null!");
            return null;
        }
        return Order.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .userEmail(dto.getUserEmail())
                .items(orderItemReadMapper.toListEntity(dto.getItems()))
                .status(dto.getStatus())
                .orderDate(LocalDateTime.now())
                .build();
    }

    @Override
    public List<OrderDetailsDto> toListDto(List<Order> entitiesList) {
        if (entitiesList == null) {
            log.warn("List with order entity is null!");
            return null;
        }
        List<OrderDetailsDto> dtos = new ArrayList<>();
        for (Order entity : entitiesList) {
            dtos.add(toDto(entity));
        }

        return dtos;
    }

    private List<Integer> findProductsIds(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getProductId)
                .toList();
    }

    private List<Product> findProductsByIds(List<Integer> productsIds) {
        log.debug("Try to get products by products ids from product-service");
        ProductsBatch batchResponse = productClient.findByIds(productsIds);
        if (batchResponse.getErrors() != null) {
            log.warn("Some products don't exist");
            String errorMessage = batchResponse.getErrors().stream()
                    .map(error -> error.getMessage() + "\n")
                    .collect(joining());
            throw new ProductNotFoundException(errorMessage);
        }
        log.debug("Return products from product-service");
        return batchResponse.getProducts();
    }

    private BigDecimal calculateTotalPrice(List<OrderItemReadDto> items) {
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (OrderItemReadDto item : items) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getProductPrice())));
        }
        return totalPrice;
    }
}
