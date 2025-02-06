package by.innowise.productservice.model.dto;

import by.innowise.productservice.model.entity.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStatusRequest {

    @NotNull(message = "Product status must not be null")
    private ProductStatus productStatus;

}
