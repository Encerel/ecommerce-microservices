package by.innowise.orderservice.mapper;

import by.innowise.orderservice.model.dto.OrderItemCreateDto;
import by.innowise.orderservice.model.entity.Order;
import by.innowise.orderservice.model.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderItemCreateMapper {

    public OrderItem toEntity(OrderItemCreateDto dto, Order order) {
        log.debug("Start to map orderItemCreateDto to orderItemEntity");
        if (dto == null) {
            log.warn("OrderItemCreateDto is null!");
            return null;
        }

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();

        log.info("OrderItem was mapped successfully!");
        return orderItem;
    }

    public List<OrderItem> toListEntity(List<OrderItemCreateDto> dtoList, Order order) {
        if (dtoList == null) {
            log.warn("List of product is empty!");
            return null;
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemCreateDto dto : dtoList) {
            orderItems.add(toEntity(dto, order));
            log.debug("Put product with id {} to order with id {}", dto.getProductId(), order.getId());
        }
        log.info("Order items was collected successfully");
        return orderItems;
    }

}
