package com.demo.controller;

import com.demo.model.User;
import com.demo.model.dto.BaseResponse;
import com.demo.service.EmailService;
import com.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/save")
    public BaseResponse<User> saveUser(@RequestBody User user){
        User savedUser=userService.saveUser(user);
        return BaseResponse.success(savedUser);
    }

    @PostMapping("/send-email")
    public BaseResponse<String> sendEmail(
            @RequestParam String email,
            @RequestParam String message
    ) {
        emailService.sendEmails(email, "Test Email", message);
        return BaseResponse.success("Email sent successfully to " + email);
    }

    @GetMapping("/all")
    public BaseResponse<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return BaseResponse.success(userService.findAll(page,size));
    }


}
