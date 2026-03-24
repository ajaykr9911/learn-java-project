package com.demo.repo;

import com.demo.model.ChatGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRepository extends MongoRepository<ChatGroup, String> {
}