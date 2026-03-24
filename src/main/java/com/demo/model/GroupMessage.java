package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "group_messages")
@Data
public class GroupMessage {

    @Id
    private String id;

    private String groupId;

    private String senderId;
    private String senderName;

    private String content;

    private LocalDateTime timestamp;
}