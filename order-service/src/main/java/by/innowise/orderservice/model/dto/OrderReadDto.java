package by.innowise.orderservice.model.dto;

import by.innowise.orderservice.model.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderReadDto {

    private Integer id;
    private UUID userId;
    private OrderStatus status;
    private LocalDate orderDate;
    private List<OrderItemReadDto> items;
    private BigDecimal totalPrice;

}
