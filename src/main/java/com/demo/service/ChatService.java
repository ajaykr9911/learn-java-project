package com.demo.service;

import com.demo.model.ChatMessage;
import com.demo.model.User;
import com.demo.model.dto.ChatRequest;
import com.demo.repo.ChatRepository;
import com.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatProducer chatProducer;


    public void processMessage(ChatRequest request) {

        ChatMessage message = new ChatMessage();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setType(request.getType());
        message.setSeq(request.getSeq());


        chatRepository.save(message);

//        System.out.println(0/0);

        boolean isOnline = OnlineUsers.isOnline(request.getReceiverId());

        if (isOnline) {
            try {
                messagingTemplate.convertAndSendToUser(
                        request.getReceiverId(),
                        "/queue/messages",
                        message
                );

                message.setStatus("DELIVERED");
                chatRepository.save(message);

            } catch (Exception e) {
                log.error("Send failed, retry later");
            }
        }

        // sender ko always send
        messagingTemplate.convertAndSendToUser(
                request.getSenderId(),
                "/queue/messages",
                message
        );
    }

    //    @Scheduled(fixedDelay = 5000)
    public void retryUndeliveredMessages() {

        List<ChatMessage> pending =
                chatRepository.findByStatus("SENT");

        if (pending.isEmpty()) return;

        log.info("Retrying {} messages", pending.size());

        for (ChatMessage msg : pending) {

            boolean isOnline = OnlineUsers.isOnline(msg.getReceiverId());

            if (!isOnline) continue;

            try {
                messagingTemplate.convertAndSendToUser(
                        msg.getReceiverId(),
                        "/queue/messages",
                        msg
                );

                msg.setStatus("DELIVERED");

                chatRepository.save(msg);

                log.info("Delivered message {}", msg.getId());

            } catch (Exception e) {
                log.error("Retry failed for {}", msg.getId());
            }
        }
    }

    public void deleteConversation(String user1, String user2) {
        // Delete messages in both directions
        chatRepository.deleteBySenderIdAndReceiverId(user1, user2);
        chatRepository.deleteBySenderIdAndReceiverId(user2, user1);
    }
}