package com.domain.backend.controller;

import com.domain.backend.dto.response.UserDto;
import com.domain.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Mono<UserDto> getCurrentUserProfile() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication()) // Lấy Authentication từ SecurityContext
                .cast(Authentication.class)
                .map(auth -> (UserDetails) auth.getPrincipal())
                .flatMap(userDetails -> userService.getUserProfile(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // Uncomment and configure if using method security
    public Mono<UserDto> getUserProfileById(@PathVariable String id) {
        return userService.getUserProfile(id);
    }
}