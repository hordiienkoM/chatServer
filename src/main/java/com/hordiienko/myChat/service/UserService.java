package com.hordiienko.myChat.service;

import com.hordiienko.myChat.dto.AuthDto;
import com.hordiienko.myChat.dto.JwtTokenDto;
import com.hordiienko.myChat.entity.User;
import com.hordiienko.myChat.exception.UserAlreadyExistException;
import com.hordiienko.myChat.repository.UserRepository;
import com.hordiienko.myChat.security.JwtTokenProvider;
import com.hordiienko.myChat.security.ReactiveCredentialsService;
import com.hordiienko.myChat.security.Role;
import com.hordiienko.myChat.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveCredentialsService<User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public Mono<JwtTokenDto> register(String username, String pass) {
        return this.checkUserAlreadyExists(username)
                .flatMap(userExists -> {
                    if (userExists) {
                        return Mono.error(new ValidationException("User with name '" + username + "' already exists"));
                    } else {
                        User user = new User();
                        user.setUsername(username);
                        String encodedPass = passwordEncoder.encode(pass);
                        user.setPassword(encodedPass);
                        user.setRoles(Set.of(Role.ROLE_USER));
                        return this.save(user)
                                .then(this.login(new AuthDto(username, pass)));
                    }
                });
    }

    public Mono<JwtTokenDto> login(AuthDto authDto) {
        return getByUsername(authDto.getLogin())
                .switchIfEmpty(Mono.error(new ValidationException(authDto.getLogin() + " user not found")))
                .map(user -> {
                    if (passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
                        String token = tokenProvider.generateToken(user);
                        user.setToken(token);
                        user = this.save(user).block();
                        return new JwtTokenDto(user.getToken());
                    } else {
                        throw new ValidationException("Incorrect login or password");
                    }
                });
    }

    @Override
    public Mono<User> getByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(username + " - user not found")));
    }

    @Override
    public Mono<User> getById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    @Override
    public Mono<User> changePassword(User user, String newPassword) {
        String encodedPass = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPass);
        return this.save(user);
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    private Mono<Boolean> checkUserAlreadyExists(String username) {
        return userRepository.findByUsername(username)
                .hasElement();
    }

    public Mono<Void> logout(User user) {
        user.setToken(null);
        userRepository.save(user);
        return Mono.empty();
    }
}
