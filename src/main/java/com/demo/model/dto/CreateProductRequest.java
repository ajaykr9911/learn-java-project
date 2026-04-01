package com.demo.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateProductRequest {

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

    private Map<String, String> attributes;

    private boolean active;
}