package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
@Data
public class Cart {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime updatedAt;

    @Data
    public static class CartItem {
        private String productId;
        private String name;
        private String image;
        private double price;
        private double discountPrice;
        private int quantity;

        public double effectivePrice() {
            return discountPrice > 0 ? discountPrice : price;
        }

        public double lineTotal() {
            return effectivePrice() * quantity;
        }
    }
}