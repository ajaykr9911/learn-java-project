package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "urls")
@Data
public class UrlShorter{
    @Id
    private String id;

    private String originalUrl;
    private String shortId;
    private long createdAt;
}
