package by.innowise.inventoryservice.web.payload.response;

import by.innowise.inventoryservice.model.api.OutStockProduct;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import lombok.Value;

import java.util.List;

@Value
public class OutStockProductResponse implements ServerResponse {

    String message;
    Integer code;
    List<OutStockProduct> outStockProducts;

}
