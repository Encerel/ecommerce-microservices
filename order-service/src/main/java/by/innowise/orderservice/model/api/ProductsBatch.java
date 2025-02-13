package by.innowise.orderservice.model.api;

import by.innowise.orderservice.web.payload.response.AdviceErrorMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductsBatch {

    List<Product> products;
    List<AdviceErrorMessage> errors;

}
