package by.innowise.orderservice.model.api;

import by.innowise.orderservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private ProductStatus status;

}
