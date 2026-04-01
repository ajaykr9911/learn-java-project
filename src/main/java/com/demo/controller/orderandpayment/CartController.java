package com.demo.controller.orderandpayment;
// ═══════════════════════════════════════════════════════════════
// CartController.java
// ═══════════════════════════════════════════════════════════════

import com.demo.config.JwtUtil;
import com.demo.model.dto.CartRequest;
import com.demo.model.dto.CartResponse;
import com.demo.service.oderservice.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    private String userId(HttpServletRequest req) {
        return jwtUtil.getUserIdFromToken(jwtUtil.resolveToken(req));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(HttpServletRequest req) {
        return ResponseEntity.ok(cartService.getCart(userId(req)));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody CartRequest request,
            HttpServletRequest req) {
        return ResponseEntity.ok(cartService.addToCart(userId(req), request));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateQuantity(
            @RequestBody CartRequest request,
            HttpServletRequest req) {
        return ResponseEntity.ok(cartService.updateQuantity(userId(req), request));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String productId,
            HttpServletRequest req) {
        return ResponseEntity.ok(cartService.removeItem(userId(req), productId));
    }
}
 