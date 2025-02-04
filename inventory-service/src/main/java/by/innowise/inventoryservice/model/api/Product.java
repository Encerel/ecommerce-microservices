package by.innowise.inventoryservice.model.api;

import by.innowise.inventoryservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private ProductStatus status;

}
