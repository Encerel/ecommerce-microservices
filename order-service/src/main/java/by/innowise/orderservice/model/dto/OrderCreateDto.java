package by.innowise.orderservice.model.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateDto {

    private UUID userId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemCreateDto> items;
}
