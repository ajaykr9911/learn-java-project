//package com.demo.service;
//
//import com.demo.model.ChatMessage;
//import com.demo.repo.ChatRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ChatConsumer {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatRepository chatRepository;
//
//    @KafkaListener(
//            topics = "chat-topic",
//            groupId = "chat-group"
//    )
//    public void consume(ChatMessage message) {
//
//        log.info("Processing message {}", message.getId());
//
//        boolean isOnline = OnlineUsers.isOnline(message.getReceiverId());
//
//        if (!isOnline) {
//            log.info("User offline → keep SENT");
//            return;
//        }
//
//        try {
//            messagingTemplate.convertAndSendToUser(
//                    message.getReceiverId(),
//                    "/queue/messages",
//                    message
//            );
//
//            message.setStatus("DELIVERED");
//            chatRepository.save(message);
//
//        } catch (Exception e) {
//            log.error("Delivery failed → Kafka retry");
//
//            throw e;
//        }
//    }
//}