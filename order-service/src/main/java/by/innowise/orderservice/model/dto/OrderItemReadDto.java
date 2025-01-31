package by.innowise.orderservice.model.dto;

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
    private Integer quantity;

}
