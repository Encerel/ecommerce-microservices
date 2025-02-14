package by.innowise.orderservice.web.controller.advice;

import by.innowise.orderservice.constant.ErrorMessage;
import by.innowise.orderservice.exception.*;
import by.innowise.orderservice.web.payload.ServerResponse;
import by.innowise.orderservice.web.payload.response.AdviceErrorMessage;
import by.innowise.orderservice.web.payload.response.OutStockProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class OrderControllerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ProductOutStockException.class)
    public ResponseEntity<ServerResponse> handleProductOutStockException(ProductOutStockException ex) {
        ServerResponse response = new OutStockProductResponse(
                ErrorMessage.NOT_ENOUGH_ITEMS_IN_STOCK,
                HttpStatus.CONFLICT.value(),
                ex.getOutStockProducts()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ServerResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        ServerResponse response = new AdviceErrorMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ServerResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        ServerResponse response = new AdviceErrorMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderAlreadyCanceledException.class)
    public ResponseEntity<ServerResponse> handleOrderAlreadyCanceledException(OrderAlreadyCanceledException ex) {
        ServerResponse response = new AdviceErrorMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownResponseException.class)
    public ResponseEntity<ServerResponse> handleUnknownResponseException(UnknownResponseException ex) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorParsingResponseException.class)
    public ResponseEntity<ServerResponse> handleErrorParsingResponseException(ErrorParsingResponseException ex) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderStatusUpdateException.class)
    public ResponseEntity<ServerResponse> handleInvalidOrderStatusUpdateException(InvalidOrderStatusUpdateException ex) {
        ServerResponse serverResponse = AdviceErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(serverResponse, HttpStatus.BAD_REQUEST);
    }
}
