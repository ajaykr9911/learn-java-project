package com.demo.model.dto;

import lombok.Data;

@Data
public class OrderResponseDto {
    private String orderId;
    private String status;
    private double amount;
}