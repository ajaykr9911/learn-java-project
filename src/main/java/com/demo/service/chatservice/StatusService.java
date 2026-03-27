package com.demo.service.chatservice;

import com.demo.model.Status;
import com.demo.repo.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Status createStatus(String userId, String content, String type) {

        Status status = new Status();
        status.setUserId(userId);
        status.setContent(content);
        status.setType(type);

        status.setCreatedAt(LocalDateTime.now());
        status.setExpiresAt(LocalDateTime.now().plusHours(24));

        return statusRepository.save(status);
    }

    public List<Status> getActiveStatuses() {
        return statusRepository.findByExpiresAtAfter(LocalDateTime.now());
    }

    public void viewStatus(String statusId, String userId) {
        Status s = statusRepository.findById(statusId).orElseThrow();

        if (!s.getViewers().contains(userId)) {
            s.getViewers().add(userId);
        }

        statusRepository.save(s);
    }

    public void likeStatus(String statusId, String userId) {
        Status s = statusRepository.findById(statusId).orElseThrow();

        if (!s.getLikes().contains(userId)) {
            s.getLikes().add(userId);
        }

        statusRepository.save(s);
    }

    public void viewLiveStatus(String statusId, String userId) {
        Status s = statusRepository.findById(statusId).orElseThrow();

        if (!s.getViewers().contains(userId)) {
            s.getViewers().add(userId);
            statusRepository.save(s);

            messagingTemplate.convertAndSendToUser(
                    s.getUserId(),
                    "/queue/status",
                    Map.of("type", "VIEW", "statusId", statusId, "userId", userId)
            );
        }
    }

    public void likeLiveStatus(String statusId, String userId) {
        Status s = statusRepository.findById(statusId).orElseThrow();

        if (!s.getLikes().contains(userId)) {
            s.getLikes().add(userId);
            statusRepository.save(s);

            messagingTemplate.convertAndSendToUser(
                    s.getUserId(),
                    "/queue/status",
                    Map.of("type", "LIKE", "statusId", statusId, "userId", userId)
            );
        }
    }
}