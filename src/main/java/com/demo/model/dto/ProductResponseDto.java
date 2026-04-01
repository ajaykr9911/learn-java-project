package com.demo.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductResponseDto {

    private String id;

    private String name;
    private String description;
    private String brand;

    private double price;
    private double discountPrice;

    private String category;
    private String subCategory;

    private String images;

    private double rating;
    private int reviewCount;

    private int stock;
    private boolean inStock;

    private Map<String, String> attributes;

    private boolean active;
}