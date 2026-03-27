package com.demo.model.dto;

import lombok.Data;

@Data
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private double price;
    private boolean active;
}