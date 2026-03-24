package com.demo.controller;

import com.demo.model.ChatGroup;
import com.demo.model.GroupMessage;
import com.demo.model.dto.CreateGroupRequest;
import com.demo.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    // ✅ create group
    @PostMapping
    public ChatGroup createGroup(@RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    // ✅ get messages
    @GetMapping("/messages")
    public List<GroupMessage> getMessages(
            @RequestParam String groupId,
            @RequestParam(defaultValue = "0") int page
    ) {
        return groupService.getGroupMessages(groupId, page);
    }
}