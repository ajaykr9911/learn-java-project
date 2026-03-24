package com.demo.controller;

import com.demo.model.ChatMessage;
import com.demo.model.TypingMessage;
import com.demo.repo.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<ChatMessage> getChat(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );

        Page<ChatMessage> result =
                chatRepository.findConversation(user1, user2, pageable);

        return result.getContent();
    }

    @PutMapping("/read")
    public void markAsRead(
            @RequestParam String senderId,
            @RequestParam String receiverId
    ) {
        List<ChatMessage> messages =
                chatRepository.findBySenderIdAndReceiverId(senderId, receiverId);

        for (ChatMessage msg : messages) {
            if (!"READ".equals(msg.getStatus())) {
                msg.setStatus("READ");
            }
        }

        chatRepository.saveAll(messages);

        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/read",
                messages
        );
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingMessage typingMessage) {
        messagingTemplate.convertAndSendToUser(
                typingMessage.getReceiverId(),
                "/queue/typing",
                typingMessage
        );
    }
}