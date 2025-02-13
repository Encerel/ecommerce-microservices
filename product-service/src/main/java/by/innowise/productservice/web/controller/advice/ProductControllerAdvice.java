package by.innowise.productservice.web.controller.advice;

import by.innowise.productservice.exception.ProductNotFoundException;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ServerResponse> handleNotFoundUserException(ProductNotFoundException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.NOT_FOUND);
    }
}
