package by.innowise.productservice.model.dto;

import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsBatchReadDto {

    List<ProductReadDto> products;
    List<AdviceErrorMessage> errors;

}
