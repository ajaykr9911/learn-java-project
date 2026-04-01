package com.demo.controller.orderandpayment;

import com.demo.config.JwtUtil;
import com.demo.model.dto.OrderResponse;
import com.demo.service.oderservice.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    private String userId(HttpServletRequest req) {
        return jwtUtil.getUserIdFromToken(jwtUtil.resolveToken(req));
    }

    // POST /api/v1/orders  — creates order from cart
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(HttpServletRequest req) {
        return ResponseEntity.ok(orderService.createOrderFromCart(userId(req)));
    }

    // GET /api/v1/orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(HttpServletRequest req) {
        return ResponseEntity.ok(orderService.getMyOrders(userId(req)));
    }

    // GET /api/v1/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String id,
            HttpServletRequest req) {
        return ResponseEntity.ok(orderService.getOrderById(id, userId(req)));
    }

    // PATCH /api/v1/orders/{id}/confirm  — COD confirmation
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmCOD(
            @PathVariable String id,
            HttpServletRequest req) {
        return ResponseEntity.ok(orderService.confirmCOD(id, userId(req)));
    }
}