package com.demo.model.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String userId;
    private String productId;
}