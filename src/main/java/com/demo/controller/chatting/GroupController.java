package com.demo.controller.chatting;

import com.demo.model.ChatGroup;
import com.demo.model.GroupMessage;
import com.demo.model.dto.CreateGroupRequest;
import com.demo.service.chatservice.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable String groupId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            groupService.deleteGroup(groupId);
            return ResponseEntity.ok(Map.of("message", "Group deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}