package by.innowise.orderservice.model.dto;


import by.innowise.orderservice.model.api.ProductQuantity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "User id should not be empty")
    private UUID userId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<ProductQuantity> items;
}
