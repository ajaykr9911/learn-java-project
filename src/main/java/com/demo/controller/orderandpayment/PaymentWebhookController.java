package com.demo.controller.orderandpayment;

import com.demo.model.Order;
import com.demo.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final OrderRepository orderRepository;

    @PostMapping("/payment")
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload) {

        String orderId = (String) payload.get("orderId");
        String status = (String) payload.get("status");
        String paymentId = (String) payload.get("paymentId");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Idempotency check
        if (!"PENDING".equals(order.getStatus())) {
            return ResponseEntity.ok("Already processed");
        }

        order.setStatus(status);
        order.setPaymentId(paymentId);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return ResponseEntity.ok("Webhook processed");
    }
}
