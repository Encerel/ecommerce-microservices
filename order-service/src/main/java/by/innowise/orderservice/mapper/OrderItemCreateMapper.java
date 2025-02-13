package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.api.ProductQuantity;
import by.innowise.orderservice.model.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderItemCreateMapper implements Mapper<OrderItem, ProductQuantity> {

    @Override
    public OrderItem toEntity(ProductQuantity dto) {
        log.debug("Start to map orderItemCreateDto to orderItemEntity");
        if (dto == null) {
            log.warn("OrderItemCreateDto is null!");
            return null;
        }

        OrderItem orderItem = OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .inventoryId(dto.getInventoryId())
                .build();

        log.info("OrderItem was mapped successfully!");
        return orderItem;
    }

    @Override
    public List<OrderItem> toListEntity(List<ProductQuantity> dtoList) {
        if (dtoList == null) {
            log.warn("List of product is empty!");
            return null;
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (ProductQuantity dto : dtoList) {
            orderItems.add(toEntity(dto));
        }
        log.info("Order items was collected successfully");
        return orderItems;
    }

}
