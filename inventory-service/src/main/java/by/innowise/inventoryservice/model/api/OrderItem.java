package by.innowise.inventoryservice.model.api;

import by.innowise.inventoryservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderItem {

    private Integer productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private ProductStatus status;
    private Integer quantity;
    private Integer inventoryId;

}
