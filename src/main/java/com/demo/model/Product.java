package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
public class Product {

    @Id
    private String id;

    private String name;
    private String description;

    private double price;
    private double discountPrice;

    private String brand;
    private String category;
    private String subCategory;

    private int stock;
    private String sku;

    private String images;

    private double rating;
    private int reviewCount;

    private Map<String, String> attributes;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}