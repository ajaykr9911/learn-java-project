package com.demo.repo;

import com.demo.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatusRepository extends MongoRepository<Status, String> {

    List<Status> findByExpiresAtAfter(LocalDateTime now);
}
