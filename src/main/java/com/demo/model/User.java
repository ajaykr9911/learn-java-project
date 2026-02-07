package com.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
@CompoundIndexes({
        @CompoundIndex(def = "{'firstName': 1, 'email': 1}")
})
public class User {

    @Id
    private String id;

    @Indexed
    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;
}

