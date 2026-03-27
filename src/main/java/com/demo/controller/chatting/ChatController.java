package com.demo.controller.chatting;

import com.demo.model.dto.ChatRequest;
import com.demo.repo.ChatRepository;
import com.demo.service.chatservice.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRepository chatRepository;


    @MessageMapping("/chat")
    public void chat(ChatRequest request) {
        chatService.processMessage(request);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteConversation(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestHeader("Authorization") String authHeader) {
        try {
            chatService.deleteConversation(user1, user2);
            return ResponseEntity.ok(Map.of("message", "Conversation deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }



}