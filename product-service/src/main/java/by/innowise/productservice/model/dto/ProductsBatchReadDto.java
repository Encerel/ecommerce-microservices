package by.innowise.productservice.model.dto;

import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductsBatchReadDto {

    List<ProductReadDto> products;
    List<AdviceErrorMessage> errors;

}
