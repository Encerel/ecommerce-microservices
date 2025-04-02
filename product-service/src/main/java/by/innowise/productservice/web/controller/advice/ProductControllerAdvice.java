package by.innowise.productservice.web.controller.advice;

import by.innowise.productservice.exception.*;
import by.innowise.productservice.web.payload.ServerResponse;
import by.innowise.productservice.web.payload.response.AdviceErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ProductControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ServerResponse> handleNotFoundUserException(ProductNotFoundException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ServerResponse> handleProductAlreadyExistsException(ProductAlreadyExistsException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<ServerResponse> handleInventoryItemNotFoundException(InventoryItemNotFoundException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInStockException.class)
    public ResponseEntity<ServerResponse> handleProductInStockException(ProductInStockException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.warn(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownResponseException.class)
    public ResponseEntity<ServerResponse> handleUnknownResponseException(UnknownResponseException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorParsingResponseException.class)
    public ResponseEntity<ServerResponse> handleErrorParsingResponseException(ErrorParsingResponseException exception) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }
}
