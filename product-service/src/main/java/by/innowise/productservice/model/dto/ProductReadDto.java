package by.innowise.productservice.model.dto;

import by.innowise.productservice.model.entity.ProductStatus;
import jakarta.validation.constraints.NotEmpty;
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
public class ProductReadDto {

    @NotNull(message = "Product id must be not null!")
    @Positive(message = "Product id must be over than zero")
    private Integer id;

    @NotEmpty(message = "Product name must not be null or empty!")
    private String name;

    @NotEmpty(message = "Product description must not be null or empty!")
    private String description;

    @Positive(message = "Product price must be over than zero")
    private Double price;

    @NotNull(message = "Product status must not be null or empty!")
    private ProductStatus status;

}
