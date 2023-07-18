package com.hordiienko.myChat.service;

import com.hordiienko.myChat.dto.AuthDto;
import com.hordiienko.myChat.dto.JwtTokenDto;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.security.JwtTokenProvider;
import com.hordiienko.myChat.security.Role;
import com.hordiienko.myChat.security.SimplePasswordEncoder;
import com.hordiienko.myChat.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private SimplePasswordEncoder passwordEncoder;

    public Mono<JwtTokenDto> login(AuthDto authDto) {
        return userService.getByUsername(authDto.getLogin())
                .switchIfEmpty(Mono.error(new ValidationException(authDto.getLogin() + " user not found")))
                .map(user -> {
                    if (passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
                        String token = tokenProvider.generateToken(user);
                        user.setToken(token);
                        userService.save(user);
                        return new JwtTokenDto(token);
                    } else {
                        throw new ValidationException("Incorrect login or password");
                    }
                });
    }

    public Mono<JwtTokenDto> register(String username, String pass) {
        return Mono.defer(() -> {
            User user = new User();
            user.setUsername(username);
            String encodedPass = passwordEncoder.encode(pass);
            user.setPassword(encodedPass);
            user.setRoles(Set.of(Role.ROLE_USER));
            String token = tokenProvider.generateToken(user);
            user.setToken(token);
            userService.save(user);
            return Mono.just(new JwtTokenDto(token));
        });
    }
}
