package com.demo.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String password;
}
