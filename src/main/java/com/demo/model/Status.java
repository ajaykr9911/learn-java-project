package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "status")
@Data
public class Status {

    @Id
    private String id;

    private String userId;
    private String content; // image/video url
    private String type; // IMAGE / VIDEO

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; // +24 hours

    private List<String> viewers = new ArrayList<>();
    private List<String> likes = new ArrayList<>();   // ❤️
}