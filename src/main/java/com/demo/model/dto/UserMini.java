// 📁 src/main/java/com/demo/model/dto/UserMini.java

package com.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMini {
    private String id;
    private String firstName;
    private String lastName;
}