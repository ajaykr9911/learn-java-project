package com.demo.model.dto;

import lombok.Data;

@Data
class AddToCartRequest {
    private String productId;
    private int quantity;
}
