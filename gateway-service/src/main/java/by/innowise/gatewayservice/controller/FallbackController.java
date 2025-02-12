package by.innowise.gatewayservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static by.innowise.gatewayservice.constant.Message.RATE_LIMIT_EXCEEDED_PLEASE_TRY_AGAIN_LATER;
import static by.innowise.gatewayservice.constant.Message.SERVICE_IS_TEMPORARILY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER;

@RestController
@RequestMapping("/fallback")
public class FallbackController {


    @GetMapping("/rateLimit")
    public ResponseEntity<String> rateLimit() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(RATE_LIMIT_EXCEEDED_PLEASE_TRY_AGAIN_LATER);
    }

    @RequestMapping("/circuitBreaker")
    public ResponseEntity<String> circuitBreaker() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(SERVICE_IS_TEMPORARILY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER);
    }

}
