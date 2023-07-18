package com.hordiienko.myChat.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface ReactiveCredentialsService<T extends StoredUserDetails> extends ReactiveUserDetailsService,
        ReactiveUserDetailsPasswordService, ReactiveAuthenticationManager {
    Mono<T> getById(String id);
    Mono<T> getByUsername(String username);
    Mono<T> save(T credentials);
    Mono<T> changePassword(T credentials, String newPassword);
    @Override
    default Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
    @Override
    default Mono<UserDetails> findByUsername(String username) {
        return getByUsername(username).cast(UserDetails.class);
    }
    @Override
    @SuppressWarnings("unchecked")
    default Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return changePassword((T) user, newPassword).cast(UserDetails.class);
    }

}
