package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.exception.OrderNotFoundException;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.entity.Order;
import by.innowise.orderservice.model.entity.OrderItem;
import by.innowise.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemReadMapper implements Mapper<OrderItem, OrderItemReadDto> {

    private final OrderRepository orderRepository;

    @Override
    public OrderItemReadDto toDto(OrderItem entity) {
        if (entity == null) {
            return null;
        }

        return OrderItemReadDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .build();
    }

    @Override
    public OrderItem toEntity(OrderItemReadDto dto) {
        if (dto == null) {
            return null;
        }
        return OrderItem.builder()
                .order(getOrder(dto.getOrderId()))
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();
    }

    @Override
    public List<OrderItemReadDto> toListDto(List<OrderItem> entitiesList) {
        if (entitiesList == null) {
            return null;
        }

        List<OrderItemReadDto> dtoList = new ArrayList<>();

        for (OrderItem entity : entitiesList) {
            dtoList.add(toDto(entity));
        }
        return dtoList;
    }

    public List<OrderItem> toListEntity(List<OrderItemReadDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemReadDto dto : dtoList) {
            orderItems.add(toEntity(dto));
        }
        return orderItems;

    }


    private Order getOrder(Integer id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
    }
}
