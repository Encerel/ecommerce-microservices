package by.innowise.productservice.model.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateDto {

    @NotEmpty(message = "Product name must not be empty!")
    private String name;

    @NotEmpty(message = "Product description must not be empty!")
    private String description;

    @Positive(message = "Product price must be over than zero!")
    private Double price;

    @Positive(message = "Product quantity must be over than zero!")
    private Integer quantity;

    @Positive(message = "Inventory id must be over than zero!")
    private Integer inventoryId;

}
