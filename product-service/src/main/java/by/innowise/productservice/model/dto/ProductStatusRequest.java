package by.innowise.productservice.model.dto;

import by.innowise.productservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStatusRequest {

    private ProductStatus productStatus;

}
