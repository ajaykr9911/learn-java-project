package com.demo.model.dto;

import lombok.Data;

@Data
public class CheckoutResponse {
    private String clientSecret;
    private String paymentIntentId;
    private String orderId;
    private double amount;
}