package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.api.Product;
import by.innowise.orderservice.model.dto.OrderItemReadDto;
import by.innowise.orderservice.model.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemReadMapper implements Mapper<OrderItem, OrderItemReadDto> {

    public OrderItemReadDto toDto(OrderItem entity, Product product) {
        if (entity == null) {
            return null;
        }

        return OrderItemReadDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProductId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productPrice(product.getPrice())
                .status(product.getStatus())
                .quantity(entity.getQuantity())
                .inventoryId(entity.getInventoryId())
                .build();
    }

    @Override
    public OrderItem toEntity(OrderItemReadDto dto) {
        if (dto == null) {
            return null;
        }
        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .inventoryId(dto.getInventoryId())
                .build();
    }

    public List<OrderItemReadDto> toListDto(List<OrderItem> entitiesList, List<Product> products) {
        if (entitiesList == null) {
            return null;
        }
        List<OrderItemReadDto> dtoList = new ArrayList<>();
        for (int i = 0; i < entitiesList.size(); i++) {
            dtoList.add(toDto(entitiesList.get(i), products.get(i)));
        }
        return dtoList;
    }

    @Override
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

}
