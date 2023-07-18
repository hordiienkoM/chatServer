package com.hordiienko.myChat.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hordiienko.myChat.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Autowired
    private Algorithm algorithm;
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getId())
                .withClaim("ema", user.getUsername())
                .withClaim("pwd", user.getPassword())
                .withArrayClaim("typ", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new)
                )
                .sign(algorithm);
    }
}
