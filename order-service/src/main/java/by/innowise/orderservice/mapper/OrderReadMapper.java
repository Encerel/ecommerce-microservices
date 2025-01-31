package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.dto.OrderReadDto;
import by.innowise.orderservice.model.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderReadMapper implements Mapper<Order, OrderReadDto> {

    private final OrderItemReadMapper orderItemReadMapper;

    @Override
    public OrderReadDto toDto(Order entity) {
        if (entity == null) {
            return null;
        }
        return OrderReadDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(orderItemReadMapper.toListDto(entity.getItems()))
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
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
}
