package com.demo.service.oderservice;

import com.demo.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FakePaymentGatewayService {

    private final RestTemplate restTemplate;

    public void initiatePayment(Order order) {

        new Thread(() -> {
            try {
                Thread.sleep(1000); // simulate delay

                String webhookUrl = "http://localhost:8080/api/v1/users/webhook/payment";

                Map<String, Object> payload = new HashMap<>();
                payload.put("orderId", order.getId());
                payload.put("status", Math.random() > 0.2 ? "SUCCESS" : "FAILED");
                payload.put("paymentId", UUID.randomUUID().toString());

                restTemplate.postForEntity(webhookUrl, payload, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}