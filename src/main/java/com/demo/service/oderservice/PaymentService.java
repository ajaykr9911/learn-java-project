package com.demo.service.oderservice;

import com.demo.exception.CustomException;
import com.demo.model.Order;
import com.demo.model.Payment;
import com.demo.model.dto.CheckoutResponse;
import com.demo.repo.OrderRepository;
import com.demo.repo.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public CheckoutResponse createPaymentIntent(String orderId, String userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (!"CREATED".equals(order.getStatus())) {
            throw new CustomException("Order is not in a payable state");
        }

        // ✅ Idempotency: if a PENDING payment already exists, return it — don't create a new one
        Optional<Payment> existing = paymentRepository.findByOrderIdAndStatus(orderId, "PENDING");
        if (existing.isPresent()) {
            try {
                PaymentIntent intent = PaymentIntent.retrieve(existing.get().getPaymentIntentId());
                CheckoutResponse res = new CheckoutResponse();
                res.setClientSecret(intent.getClientSecret());
                res.setPaymentIntentId(intent.getId());
                res.setOrderId(orderId);
                res.setAmount(existing.get().getAmount());
                return res;
            } catch (Exception e) {
                // Stripe retrieval failed — fall through to create a fresh intent
            }
        }

        try {
            long amountInPaise = Math.round(order.getTotalAmount() * 100);

            PaymentIntent intent = PaymentIntent.create(
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .putMetadata("orderId", orderId)
                            .putMetadata("userId", userId)
                            .build()
            );

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(userId);
            payment.setPaymentIntentId(intent.getId());
            payment.setAmount(order.getTotalAmount());
            payment.setCurrency("inr");
            payment.setStatus("PENDING");
            payment.setMethod("STRIPE");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            CheckoutResponse res = new CheckoutResponse();
            res.setClientSecret(intent.getClientSecret());
            res.setPaymentIntentId(intent.getId());
            res.setOrderId(orderId);
            res.setAmount(order.getTotalAmount());
            return res;

        } catch (Exception e) {
            throw new CustomException("Stripe error: " + e.getMessage());
        }
    }

    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            throw new CustomException("Invalid Stripe webhook signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElseThrow();

            String orderId = intent.getMetadata().get("orderId");

            paymentRepository.findByPaymentIntentId(intent.getId()).ifPresent(p -> {
                p.setStatus("SUCCEEDED");
                p.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(p);
            });

            orderService.markAsPaid(orderId);

        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElseThrow();

            paymentRepository.findByPaymentIntentId(intent.getId()).ifPresent(p -> {
                p.setStatus("FAILED");
                p.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(p);
            });
        }
    }
}