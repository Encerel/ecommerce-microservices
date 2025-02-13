package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.api.Product;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.dto.OrderReadDto;
import by.innowise.orderservice.model.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderReadMapper implements Mapper<Order, OrderReadDto> {

    private final OrderItemReadMapper orderItemReadMapper;

    public OrderReadDto toDto(Order entity, List<Product> products) {
        if (entity == null) {
            return null;
        }
        List<OrderItemReadDto> orderItems = orderItemReadMapper.toListDto(entity.getItems(), products);
        BigDecimal totalPrice = calculateTotalPrice(orderItems);
        return OrderReadDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(orderItems)
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public Order toEntity(OrderReadDto dto) {
        if (dto == null) {
            return null;
        }
        return Order.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .items(orderItemReadMapper.toListEntity(dto.getItems()))
                .status(dto.getStatus())
                .orderDate(LocalDate.now())
                .build();
    }

    private BigDecimal calculateTotalPrice(List<OrderItemReadDto> items) {
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (OrderItemReadDto item : items) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getProductPrice())));
        }
        return totalPrice;
    }
}
