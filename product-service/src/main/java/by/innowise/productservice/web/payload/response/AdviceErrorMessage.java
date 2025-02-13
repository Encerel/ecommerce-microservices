package by.innowise.productservice.web.payload.response;

import by.innowise.productservice.web.payload.ServerResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdviceErrorMessage implements ServerResponse {

    private String message;
    private int status;

}
