package com.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "stores")
public class Store {

    @Id
    private String id;

    private String name;

    private double latitude;

    private double longitude;

    private String address;

    private double serviceRadiusKm;
}