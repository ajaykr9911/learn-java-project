package com.demo.controller;

import com.demo.model.dto.ChatRequest;
import com.demo.repo.ChatRepository;
import com.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRepository chatRepository;


    @MessageMapping("/chat")
    public void chat(ChatRequest request) {
        chatService.processMessage(request);
    }



}