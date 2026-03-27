package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "orders")
@Data
public class Order {

    @Id
    private String id;

    private String userId;
    private String productId;
    private double amount;

    private String status; // PENDING, SUCCESS, FAILED
    private String paymentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}