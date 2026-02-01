package com.demo.service;

import com.demo.exception.CustomException;
import com.demo.model.User;
import com.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    public User saveUser(User user){
        User userResponse=userRepository.findByEmail(user.getEmail());
        if(userResponse!=null){
            throw new CustomException("User already exists");
        }
        return userRepository.save(user);
    }
}
