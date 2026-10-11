package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.CheckoutRequest;
import com.bookverse.dto.OrderDto;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    public OrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@Valid @RequestBody CheckoutRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        OrderDto order = orderService.checkout(user, request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully!", order), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(user, id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        OrderDto order = orderService.cancelOrder(user, id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }
}
