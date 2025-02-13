package by.innowise.inventoryservice.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductStock {

    private Integer productId;
    private Integer previousStock;
    private Integer currentStock;

}
