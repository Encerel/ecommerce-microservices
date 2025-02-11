package by.innowise.gatewayservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static by.innowise.gatewayservice.constant.Message.RATE_LIMIT_EXCEEDED_PLEASE_TRY_AGAIN_LATER;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(RATE_LIMIT_EXCEEDED_PLEASE_TRY_AGAIN_LATER);
    }
}
