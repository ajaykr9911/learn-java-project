package com.demo.repo;

import com.demo.model.ChatMessage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findBySenderIdAndReceiverId(String sender, String receiver);

    List<ChatMessage> findByReceiverId(String receiver);

    @Query("{ $or: [ " +
            "{ 'senderId': ?0, 'receiverId': ?1 }, " +
            "{ 'senderId': ?1, 'receiverId': ?0 } " +
            "] }")
    Page<ChatMessage> findConversation(String user1, String user2, Pageable pageable);

    List<ChatMessage> findByStatus(String sent);

    @Transactional
    @Query("DELETE FROM ChatMessage m WHERE m.senderId = :senderIdAND m.receiverId = :receiverId")
    void deleteBySenderIdAndReceiverId(
            @Param("senderId") String senderId,
            @Param("receiverId") String receiverId);


//    boolean existsByClientId(String clientId);

//    List<ChatMessage> findByStatusOrderBySeqAsc(String status);
}