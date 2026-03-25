package com.demo.model.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String senderId;
    private String receiverId;
    private String content;
    private Long seq;
}