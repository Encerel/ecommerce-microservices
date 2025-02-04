package by.innowise.orderservice.web.controller;

import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderReadDto;
import by.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderReadDto placeOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        return orderService.placeOrder(orderCreateDto);
    }

    @PostMapping("/cancel/{orderId}")
    public OrderReadDto cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }


}
