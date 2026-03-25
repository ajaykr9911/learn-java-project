package com.demo.controller;

import com.demo.model.dto.GroupMessageRequest;
import com.demo.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupSocketController {

    private final GroupService groupService;

    @MessageMapping("/group")
    public void sendGroupMessage(GroupMessageRequest request) {
        System.out.println("Received group message for: " + request.getGroupId());
        groupService.processGroupMessage(request);
    }
}
