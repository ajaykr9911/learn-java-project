package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "products")
@Data
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}