package com.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class VariantDto {

    private String id;
    private String name;
    private String color;
    private String size;

    private double price;
    private int stockQuantity;

    private List<String> images;
}