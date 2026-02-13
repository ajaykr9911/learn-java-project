package com.demo.controller;

import com.demo.model.User;
import com.demo.model.dto.BaseResponse;
import com.demo.model.dto.LoginRequest;
import com.demo.model.dto.LoginResponse;
import com.demo.service.EmailService;
import com.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public BaseResponse<User> saveUser(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody User user){
        User savedUser=userService.saveUser(idempotencyKey,user);
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String userId
    ){
        return BaseResponse.success(userService.findAll(page,size,search,userId));
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return BaseResponse.success(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return BaseResponse.success();
    }

    @GetMapping("/test")
    public BaseResponse<String> test(){
        return BaseResponse.success("Hello");
    }



}
