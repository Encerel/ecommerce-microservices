package by.innowise.orderservice.model.dto;

import by.innowise.orderservice.model.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsDto {

    private Integer id;
    private UUID userId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<OrderItemReadDto> items;
    private BigDecimal totalPrice;

}
