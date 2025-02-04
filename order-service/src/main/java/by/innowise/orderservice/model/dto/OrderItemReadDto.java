package by.innowise.orderservice.model.dto;

import by.innowise.orderservice.model.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemReadDto {

    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private ProductStatus status;
    private Integer quantity;

}
