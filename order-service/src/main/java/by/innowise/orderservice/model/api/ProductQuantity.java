package by.innowise.orderservice.model.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductQuantity {

    @NotNull(message = "Inventory id should not be null!")
    @Positive(message = "Inventory id should by over than 0!")
    private Integer inventoryId;

    @NotNull(message = "Product id should not be null!")
    @Positive(message = "Product id should by over than 0!")
    private Integer productId;

    @Positive(message = "Quantity should be over than 0!")
    private Integer quantity;

}
