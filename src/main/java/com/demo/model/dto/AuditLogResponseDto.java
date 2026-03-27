package com.demo.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogResponseDto {

    private String id;
    private String userId;
    private String userName;
    private String action;
    private String method;
    private String path;
    private String ip;
    private LocalDateTime timestamp;
}