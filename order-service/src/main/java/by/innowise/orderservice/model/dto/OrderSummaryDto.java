package by.innowise.orderservice.model.dto;

import by.innowise.orderservice.model.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderSummaryDto {

    private Integer id;
    private UUID userId;
    private OrderStatus status;
    private LocalDate orderDate;

}
