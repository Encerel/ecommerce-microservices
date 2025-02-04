package by.innowise.orderservice.web.payload.response;


import by.innowise.orderservice.model.api.OutStockProduct;
import by.innowise.orderservice.web.payload.ServerResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutStockProductResponse implements ServerResponse {

    String message;
    Integer code;
    List<OutStockProduct> outStockProducts;

}
