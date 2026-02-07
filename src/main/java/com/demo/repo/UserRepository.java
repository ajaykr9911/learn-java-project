package com.demo.repo;

import com.demo.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(@Email String email);

    Page<User> findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String search, String search1, Pageable pageable);

    Page<User> findById(String userId, Pageable pageable);
}
