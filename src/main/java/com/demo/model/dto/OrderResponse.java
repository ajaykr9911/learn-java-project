package com.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private String orderId;
    private String userId;
    private String status;
    private String paymentMethod;
    private double totalAmount;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private String productId;
        private String name;
        private String image;
        private double price;
        private double discountPrice;
        private int quantity;
        private double totalPrice;
    }
}