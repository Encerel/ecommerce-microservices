package by.innowise.productservice.model.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductQuantity {

    @NotNull(message = "Inventory id must not be null!")
    @Positive(message = "Inventory id must be over than zero!")
    private Integer inventoryId;

    @NotNull(message = "Product id must not be null!")
    @Positive(message = "Product id must be over than zero!")
    private Integer productId;

    @Positive(message = "Quantity of product must be over than zero!")
    private Integer quantity;

}
