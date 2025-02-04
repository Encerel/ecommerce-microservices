package by.innowise.orderservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemCreateDto {

    @NotNull(message = "Product id should not be null!")
    private Integer productId;

    @Size(min = 0, message = "Quantity should be more than 0!")
    private Integer quantity;

}
