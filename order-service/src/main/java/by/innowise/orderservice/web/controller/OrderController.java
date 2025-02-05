package by.innowise.orderservice.web.controller;

import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderDetailsDto;
import by.innowise.orderservice.model.dto.OrderSummaryDto;
import by.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDetailsDto placeOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        return orderService.placeOrder(orderCreateDto);
    }

    @PostMapping("/cancel/{orderId}")
    public OrderDetailsDto cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }


    @GetMapping
    public Page<OrderSummaryDto> findAllOrders(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "5") Integer pageSize) {
        return orderService.findAll(offset, pageSize);
    }

    @GetMapping("/{orderId}")
    public OrderDetailsDto findOrderById(@PathVariable Integer orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<OrderSummaryDto> findOrdersByUserId(@PathVariable UUID userId) {
        return orderService.findAllByUserId(userId);
    }

}
