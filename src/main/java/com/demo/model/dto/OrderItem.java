package com.demo.model.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OrderItem {

    private String productId;

    private String name;        // snapshot (important)
    private String image;       // snapshot

    private double price;       // actual price at purchase
    private double discountPrice;

    private int quantity;

    private double totalPrice;

    private LocalTime createdAt;
    private LocalTime updatedAt;

    // price * quantity
}