package by.innowise.inventoryservice.web.controller.advice;

import by.innowise.inventoryservice.exception.ProductNotFoundException;
import by.innowise.inventoryservice.exception.ProductOutStockException;
import by.innowise.inventoryservice.web.payload.ServerResponse;
import by.innowise.inventoryservice.web.payload.response.AdviceErrorMessage;
import by.innowise.inventoryservice.web.payload.response.OutStockProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
