package by.innowise.orderservice.mapper;

import by.innowise.mapper.Mapper;
import by.innowise.orderservice.model.dto.OrderSummaryDto;
import by.innowise.orderservice.model.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderSummaryMapper implements Mapper<Order, OrderSummaryDto> {


    @Override
    public OrderSummaryDto toDto(Order entity) {
        log.debug("Try to map order entity to OrderSummaryDto");
        if (entity == null) {
            log.warn("Order entity is null!");
            return null;
        }
        log.debug("OrderSummaryDto is ready");
        return OrderSummaryDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .build();
    }

    @Override
    public List<OrderSummaryDto> toListDto(List<Order> entitiesList) {
        log.debug("Try to map entities list to OrderSummaryDto");
        if (entitiesList == null) {
            log.warn("List with order entity is null!");
            return null;
        }
        List<OrderSummaryDto> dtos = new ArrayList<>();
        for (Order entity : entitiesList) {
            dtos.add(toDto(entity));
        }

        return dtos;
    }

}
