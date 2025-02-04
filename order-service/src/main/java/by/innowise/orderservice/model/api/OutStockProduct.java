package by.innowise.orderservice.model.api;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutStockProduct {

    Integer productId;
    Integer availableQuantity;
    Integer requiredQuantity;

}
