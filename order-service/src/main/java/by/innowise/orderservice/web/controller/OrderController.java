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

    @GetMapping("/my")
    public List<OrderSummaryDto> findOrdersByUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.findAllByUserId(UUID.fromString(jwt.getSubject()));
    }

    @PostMapping
    public OrderDetailsDto placeOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderCreateDto.setUserId(UUID.fromString(jwt.getSubject()));
        orderCreateDto.setUserEmail(jwt.getClaimAsString("email"));
        return orderService.placeOrder(orderCreateDto);
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderSummaryDto cancelOrder(@PathVariable Integer orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.CANCELED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/confirm")
    public OrderSummaryDto confirmOrder(@PathVariable Integer orderId) {
        return orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

}
