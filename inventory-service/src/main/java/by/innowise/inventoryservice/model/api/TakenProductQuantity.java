package by.innowise.inventoryservice.model.api;

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
public class TakenProductQuantity {

    @NotNull(message = "Product id must not be null!")
    @Positive(message = "Product id must be over than zero!")
    private Integer productId;

    @NotNull(message = "Product quantity must not be null!")
    @Positive(message = "Product quantity must be over than zero!")
    private Integer quantity;

}
