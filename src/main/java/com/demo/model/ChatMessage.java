package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;

    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private String senderName;
    private String receiverName;
    private String status;
}