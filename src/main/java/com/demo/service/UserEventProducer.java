//package com.demo.service;
//
//
//import com.demo.model.dto.UserCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserEventProducer {
//
//    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
//
//    public void publishUserCreated(UserCreatedEvent event) {
//        kafkaTemplate.send("user-created-topic", event.getUserId(), event);
//    }
//}