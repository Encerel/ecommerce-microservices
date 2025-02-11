package by.innowise.orderservice.web.controller;

import by.innowise.orderservice.model.dto.OrderCreateDto;
import by.innowise.orderservice.model.dto.OrderDetailsDto;
import by.innowise.orderservice.model.dto.OrderSummaryDto;
import by.innowise.orderservice.model.entity.OrderStatus;
import by.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public OrderDetailsDto placeOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderCreateDto.setUserId(UUID.fromString(jwt.getSubject()));
        return orderService.placeOrder(orderCreateDto);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/{orderId}/cancel")
    public OrderDetailsDto cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/confirm")
    public OrderDetailsDto confirmOrder(@PathVariable Integer orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<OrderSummaryDto> findAllOrders(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "5") Integer pageSize) {
        return orderService.findAll(offset, pageSize);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public OrderDetailsDto findOrderById(@PathVariable Integer orderId) {
        return orderService.findById(orderId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public List<OrderSummaryDto> findOrdersByUserId(@PathVariable UUID userId) {
        return orderService.findAllByUserId(userId);
    }

}
