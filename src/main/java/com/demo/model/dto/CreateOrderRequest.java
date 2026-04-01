package com.demo.model.dto;

import lombok.Data;



import java.util.List;

@Data
public class CreateOrderRequest {

    private Item item;

    @Data
    public static class Item {
        private String productId;
        private int quantity;
    }
}