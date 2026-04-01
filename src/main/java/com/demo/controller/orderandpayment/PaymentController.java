package com.demo.controller.orderandpayment;
// ═══════════════════════════════════════════════════════════════
// PaymentController.java
// ═══════════════════════════════════════════════════════════════


import com.demo.config.JwtUtil;
import com.demo.model.dto.CheckoutRequest;
import com.demo.model.dto.CheckoutResponse;
import com.demo.service.oderservice.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;

    private String userId(HttpServletRequest req) {
        return jwtUtil.getUserIdFromToken(jwtUtil.resolveToken(req));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request,
            HttpServletRequest req) {
        return ResponseEntity.ok(
                paymentService.createPaymentIntent(request.getOrderId(), userId(req))
        );
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}