package com.demo.model.dto;

import lombok.Data;

@Data
public class GroupMessageRequest {

    private String groupId;

    private String senderId;

    private String senderName;

    private String content;
}