package by.innowise.orderservice.web.payload.response;

import by.innowise.orderservice.web.payload.ServerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageServerResponse implements ServerResponse {

    private String message;
    private int status;

}
