package com.hordiienko.myChat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hordiienko.myChat.security.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private Set<Role> roles;
}
