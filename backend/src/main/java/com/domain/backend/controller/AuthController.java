package com.domain.backend.controller;

import com.domain.backend.dto.request.ChangePasswordRequest;
import com.domain.backend.dto.request.LoginRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.dto.response.AuthResponse;
import com.domain.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Mono<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            return authService.refreshToken(refreshToken.substring(7));
        }
        return Mono.error(new IllegalArgumentException("Refresh token is missing or invalid format"));
    }

    @PostMapping("/change-password")
    public Mono<ResponseEntity<String>> changePassword(
            @RequestBody ChangePasswordRequest req,
            Authentication authentication) {

        String username = authentication.getName();
        return authService.changePassword(username, req.getCurrentPassword(), req.getNewPassword(), req.getConfirmPassword())
                .then(Mono.just(ResponseEntity.ok("Password updated successfully")));
    }

    @PostMapping("/forgot-password")
    public Mono<ResponseEntity<String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        return authService.forgotPassword(email).map(ResponseEntity::ok);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }
}