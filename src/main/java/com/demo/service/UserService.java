package com.demo.service;

import com.demo.exception.CustomException;
import com.demo.model.User;
import com.demo.model.dto.UserCreatedEvent;
import com.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public User saveUser(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new CustomException("User already exists");
        }

        User savedUser = userRepository.save(user);

        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName()
        );

        userEventProducer.publishUserCreated(event);

        return savedUser;
    }


    public Map<String, Object> findAll(int page, int size) {
        List<User> users=userRepository.findAll();

        int totalElements = users.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<User> paginatedUsers = new ArrayList<>();

        if (startIndex < totalElements) {
            paginatedUsers = users.subList(startIndex, endIndex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", paginatedUsers);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalPages", totalPages);
        response.put("totalElements", totalElements);

        return response;
    }
}
