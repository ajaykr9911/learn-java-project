package com.demo.controller;

import com.demo.model.User;
import com.demo.model.dto.BaseResponse;
import com.demo.service.BreveEmailService;
import com.demo.service.EmailService;
import com.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final BreveEmailService breveEmailService;

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

    @PostMapping("/send-brevo-email")
    public String sendBrevoEmail(@RequestParam String email, @RequestParam String message) {
        breveEmailService.sendEmail(email, "Test Email", message);
        return "Email sent successfully to " + email;
    }

}
