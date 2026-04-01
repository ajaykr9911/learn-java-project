package com.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchProductDto {

    private String keyword;          // name / description search

    private List<String> categories; // multi-select
    private List<String> brands;

    private Double minPrice;
    private Double maxPrice;

    private Boolean inStock;
    private Boolean active;

    private Integer page = 0;
    private Integer size = 10;

    private String sortBy = "createdAt"; // price, rating, createdAt
    private String sortDir = "desc";     // asc / desc
}