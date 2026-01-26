package com.demo.repo;

import com.demo.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(@Email String email);
}
