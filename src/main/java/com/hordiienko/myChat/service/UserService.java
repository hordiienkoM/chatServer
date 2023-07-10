package com.hordiienko.myChat.service;

import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.repository.UserRepository;
import com.hordiienko.myChat.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.channels.MembershipKey;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Mono<User> register(String username, String pass) {
        return Mono.defer(() -> {
            User user = new User();
            user.setUsername(username);
            String encodedPass = passwordEncoder.encode(pass);
            user.setPassword(encodedPass);
            user.setRoles(Set.of(Role.ROLE_USER));
            return userRepository.save(user);
        });
    }


    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    public Mono<User> findUserById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }
}
