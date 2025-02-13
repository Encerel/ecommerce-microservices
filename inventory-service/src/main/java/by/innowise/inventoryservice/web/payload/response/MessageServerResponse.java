package by.innowise.inventoryservice.web.payload.response;

import by.innowise.inventoryservice.web.payload.ServerResponse;
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
