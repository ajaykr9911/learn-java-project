package com.demo.controller.chatting;

import com.demo.model.Status;
import com.demo.service.chatservice.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    @PostMapping("/create")
    public Status create(
            @RequestParam String userId,
            @RequestParam String content,
            @RequestParam String type
    ) {
        return statusService.createStatus(userId, content, type);
    }

    @GetMapping("/all")
    public List<Status> getAll() {
        return statusService.getActiveStatuses();
    }

    @PostMapping("/view")
    public void view(
            @RequestParam String statusId,
            @RequestParam String userId
    ) {
        statusService.viewStatus(statusId, userId);
    }

    @PostMapping("/like")
    public void like(
            @RequestParam String statusId,
            @RequestParam String userId
    ) {
        statusService.likeStatus(statusId, userId);
    }
}