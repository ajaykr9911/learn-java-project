package com.demo.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateGroupRequest {

    private String name;

    private String createdBy;   // userId

    private List<String> members; // list of userIds
}