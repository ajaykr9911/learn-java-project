package com.demo.service;

import com.demo.config.JwtUtil;
import com.demo.exception.CustomException;
import com.demo.model.User;
import com.demo.model.dto.LoginRequest;
import com.demo.model.dto.UserCreatedEvent;
import com.demo.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
//    private final UserEventProducer userEventProducer;




    private static final String KEY = "USER:";

    public User saveUser(String idempotencyKey, User user) {

        String idemKey = "idem:signup:" + idempotencyKey;
        String lockKey = "lock:user:email:" + user.getEmail();
        String lockValue = UUID.randomUUID().toString();

        boolean locked = acquireLock(lockKey, lockValue, 10);
        if (!locked) {
            throw new CustomException("Signup already in progress. Please retry.");
        }

        try {
            User cachedUser = (User) redisTemplate.opsForValue().get(idemKey);
            if (cachedUser != null) {
                return cachedUser;
            }

            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                redisTemplate.opsForValue()
                        .set(idemKey, existingUser, 10, TimeUnit.MINUTES);
                throw new CustomException("User already exists");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserCreatedEvent event = new UserCreatedEvent();
//            event.setUserId(savedUser.getId());
//            event.setEmail(savedUser.getEmail());
//            event.setFirstName(savedUser.getFirstName());
//
//            userEventProducer.publishUserCreated(event);

            redisTemplate.opsForValue()
                    .set(idemKey, savedUser, 10, TimeUnit.MINUTES);

            return savedUser;

        } finally {
            releaseLock(lockKey, lockValue);
        }
    }


    private static final String SEARCH_KEY = "USER_SEARCH:";

    public Map<String, Object> findAll(int page, int size, String search, String userId) {

        String cacheKey = SEARCH_KEY + page + ":" + size + ":" + search + ":" + userId;

        Map<String, Object> cachedResponse =
                (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (search == null || search.trim().isEmpty()) {
            if (userId != null) {
                userPage = userRepository.findById(userId, pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }
        } else {
            userPage = userRepository
                    .findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            search,
                            search,
                            pageable
                    );
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());

        redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);

        return response;
    }




    public User getUser(String id) {
        User cachedUser = (User) redisTemplate.opsForValue().get(KEY + id);
        if (cachedUser != null) {
            return cachedUser;
        }
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            redisTemplate.opsForValue().set(KEY + id, user);
        }

        return user;
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new CustomException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password");
        }

        String jti = UUID.randomUUID().toString();

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                jti
        );

        redisTemplate.opsForValue().set(
                "session:" + user.getId(),
                jti,
                jwtUtil.getExpirationMillis(),
                TimeUnit.MILLISECONDS
        );


        return token;
    }

    public void logout(HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            return;
        }

        String userId = jwtUtil.getUserIdFromToken(token);

        redisTemplate.delete("session:" + userId);
    }

    private boolean acquireLock(String lockKey, String lockValue, long ttlSeconds) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, ttlSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    private void releaseLock(String lockKey, String lockValue) {
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }


}
