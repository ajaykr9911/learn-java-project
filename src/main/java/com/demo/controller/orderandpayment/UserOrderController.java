package com.demo.controller.orderandpayment;

import com.demo.model.dto.CreateOrderRequest;
import com.demo.model.dto.OrderResponseDto;
import com.demo.service.oderservice.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    @PostMapping("/purchase")
    public OrderResponseDto purchase(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // New: Polling endpoint for the Frontend
    @GetMapping("/{orderId}/status")
    public OrderResponseDto getStatus(@PathVariable String orderId) {
        return orderService.getOrderStatus(orderId);
    }
}