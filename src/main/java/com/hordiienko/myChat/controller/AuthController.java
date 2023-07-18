package com.hordiienko.myChat.controller;

import com.hordiienko.myChat.dto.AuthDto;
import com.hordiienko.myChat.dto.JwtTokenDto;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;

    @MessageMapping("registration")
    public Mono<JwtTokenDto> register(AuthDto regInfo) {
        String username = regInfo.getLogin();
        String password = regInfo.getPassword();

        return userService.register(username, password);
    }

    @MessageMapping("login")
    public Mono<JwtTokenDto> login(AuthDto logInfo) {
        return userService.login(logInfo);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MessageMapping("logout")
    public Mono<Void> logout(@AuthenticationPrincipal User user) {
        return userService.logout(user);
    }
}
