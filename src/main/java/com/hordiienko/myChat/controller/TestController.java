package com.hordiienko.myChat.controller;

import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.repository.UserRepository;
import com.hordiienko.myChat.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public String test() {
        return "hello";
    }

    @GetMapping("/user")
    public String us(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userName = userDetails.getUsername();
        return "hello user - " + userName;
    }

    @PostMapping("/rewrite")
    public Mono<User> rewriteUser(String username, String pass) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(pass));
                    return userRepository.save(user);
                });
    }
}
