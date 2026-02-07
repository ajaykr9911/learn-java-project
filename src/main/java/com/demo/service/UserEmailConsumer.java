//package com.demo.service;
//
//
//import com.demo.model.dto.UserCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserEmailConsumer {
//
//    private final EmailService emailService;
//
//    @KafkaListener(
//            topics = "user-created-topic",
//            groupId = "user-group"
//    )
//    public void sendWelcomeEmail(UserCreatedEvent event) {
//
//        String message = "Welcome " + event.getFirstName() + " 🎉";
//
//        emailService.sendEmails(
//                event.getEmail(),
//                "Welcome to our platform",
//                message
//        );
//
//        System.out.println("Welcome email sent to: " + event.getEmail());
//    }
//}
