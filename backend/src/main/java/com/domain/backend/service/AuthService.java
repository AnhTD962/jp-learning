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

import java.security.SecureRandom;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final MailService mailService;

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

    public Mono<Void> changePassword(String username, String currentPassword, String newPassword, String confirmPassword) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Current password is incorrect"));
                    } else if (newPassword.equals(currentPassword)) {
                        return Mono.error(new IllegalArgumentException("New password must be different from the current password"));
                    } else if (!newPassword.equals(confirmPassword)) {
                        return Mono.error(new IllegalArgumentException("New password and confirm password must match"));
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user).then();
                });
    }

    public Mono<String> forgotPassword(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Email not found")))
                .flatMap(user -> {
                    String randomPass = generateRandomPassword();
                    user.setPassword(passwordEncoder.encode(randomPass));

                    return userRepository.save(user)
                            .flatMap(saved ->
                                    mailService.sendMail(
                                            email,
                                            "Password Reset",
                                            "Your new password is: " + randomPass
                                    ).thenReturn("New password sent to your email")
                            );
                });
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
