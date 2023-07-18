package com.hordiienko.myChat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hordiienko.myChat.security.Role;
import com.hordiienko.myChat.security.StoredUserDetails;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "users")
public class User implements StoredUserDetails {
    @Id
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private Set<Role> roles;
    String token;
    boolean isLocked;
    boolean isEnabled = true;

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
