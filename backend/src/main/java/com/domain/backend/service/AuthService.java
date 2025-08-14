package com.domain.backend.service;

import com.domain.backend.config.JwtConfig;
import com.domain.backend.dto.request.LoginRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.dto.response.AuthResponse;
import com.domain.backend.entity.Role;
import com.domain.backend.entity.User;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(existingUser -> Mono.<AuthResponse>error(new IllegalArgumentException("Username already exists")))
                .switchIfEmpty(Mono.defer(() ->
                        userRepository.findByEmail(request.getEmail())
                                .flatMap(existingEmail -> Mono.<AuthResponse>error(new IllegalArgumentException("Email already exists")))
                                .switchIfEmpty(Mono.defer(() -> {
                                    User newUser = new User();
                                    newUser.setUsername(request.getUsername());
                                    newUser.setEmail(request.getEmail());
                                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                                    newUser.setRoles(Collections.singleton(Role.STUDENT));
                                    newUser.setXpPoints(0);
                                    newUser.setStudyStreak(0);
                                    newUser.setAchievementIds(Collections.emptyList());

                                    return userRepository.save(newUser)
                                            .map(this::generateAuthResponse);
                                }))
                ));
    }


    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid credentials")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.just(generateAuthResponse(user));
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid credentials"));
                    }
                });
    }

    public Mono<AuthResponse> refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            return Mono.error(new IllegalArgumentException("Invalid refresh token"));
        }

        String username = jwtUtil.extractUsername(refreshToken);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found for refresh token")))
                .map(this::generateAuthResponse);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateToken(user, jwtConfig.getAccessTokenExpirationMs());
        String refreshToken = jwtUtil.generateToken(user, jwtConfig.getRefreshTokenExpirationMs());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
