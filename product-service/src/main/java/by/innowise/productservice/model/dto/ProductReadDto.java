package by.innowise.productservice.model.dto;

import by.innowise.productservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReadDto {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private ProductStatus status;

}
