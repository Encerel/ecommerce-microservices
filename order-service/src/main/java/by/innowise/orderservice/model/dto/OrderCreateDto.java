package by.innowise.orderservice.model.dto;


import by.innowise.orderservice.model.api.TakenProductQuantity;
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

    private String userEmail;

    @NotEmpty(message = "Order must contain at least one item")
    private List<TakenProductQuantity> items;
}
