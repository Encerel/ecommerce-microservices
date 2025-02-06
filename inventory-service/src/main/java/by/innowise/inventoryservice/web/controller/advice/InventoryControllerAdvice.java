package by.innowise.inventoryservice.web.controller.advice;

import by.innowise.inventoryservice.exception.*;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import by.innowise.inventoryservice.web.payload.response.AdviceErrorMessage;
import by.innowise.inventoryservice.web.payload.response.OutStockProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static by.innowise.inventoryservice.constant.ErrorMessage.NOT_ENOUGH_ITEMS_IN_STOCK;

@RestControllerAdvice
public class InventoryControllerAdvice {


    @ExceptionHandler(ProductOutStockException.class)
    public ResponseEntity<ServerResponse> handleProductOutStockException(ProductOutStockException e) {
        ServerResponse response = new OutStockProductResponse(
                NOT_ENOUGH_ITEMS_IN_STOCK,
                HttpStatus.CONFLICT.value(),
                e.getOutStockProducts()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ServerResponse> handleProductNotFoundException(ProductNotFoundException e) {
        ServerResponse response = new AdviceErrorMessage(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ServerResponse> handleInventoryNotFoundException(InventoryNotFoundException e) {
        ServerResponse response = new AdviceErrorMessage(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UnknownResponseException.class)
    public ResponseEntity<ServerResponse> handleUnknownResponseException(UnknownResponseException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorParsingResponseException.class)
    public ResponseEntity<ServerResponse> handleErrorParsingResponseException(ErrorParsingResponseException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

}
