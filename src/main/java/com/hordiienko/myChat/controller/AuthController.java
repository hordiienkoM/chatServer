package com.hordiienko.myChat.controller;

import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Mono<User> register(@RequestParam String username, String pass) {
        return userService.register(username, pass);
    }
}
