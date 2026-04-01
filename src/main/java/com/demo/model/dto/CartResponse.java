package com.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
    private String cartId;
    private String userId;
    private List<CartItemDto> items;
    private double cartTotal;

    @Data
    public static class CartItemDto {
        private String productId;
        private String name;
        private String image;
        private double price;
        private double discountPrice;
        private int quantity;
        private double lineTotal;
    }
}