package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "groups")
@Data
public final class ChatGroup {

    @Id
    private String id;

    private String name;

    private String createdBy;

    private List<String> members;

    private LocalDateTime createdAt;
}
