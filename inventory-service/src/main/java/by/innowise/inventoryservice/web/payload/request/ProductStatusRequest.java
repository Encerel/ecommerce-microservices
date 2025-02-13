package by.innowise.inventoryservice.web.payload.request;

import by.innowise.inventoryservice.model.entity.ProductStatus;
import by.innowise.inventoryservice.web.payload.ClientRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductStatusRequest implements ClientRequest {

    @NotNull(message = "Product status should not be null")
    private ProductStatus productStatus;

}
