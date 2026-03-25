package com.demo.repo;

import com.demo.model.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupMessageRepository extends MongoRepository<GroupMessage, String> {

    Page<GroupMessage> findByGroupId(
            String groupId,
            Pageable pageable
    );

    void deleteByGroupId(String groupId);
}