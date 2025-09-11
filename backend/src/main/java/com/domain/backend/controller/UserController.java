package com.domain.backend.controller;

import com.domain.backend.dto.request.UpdateProfileRequest;
import com.domain.backend.dto.request.UpdateUserRolesRequest;
import com.domain.backend.dto.response.UserDto;
import com.domain.backend.security.JwtUtil;
import com.domain.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public Mono<UserDto> getCurrentUserProfile() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(Authentication.class)
                .map(Authentication::getName)
                .flatMap(userService::getUserProfileByUsername);
    }

    @PutMapping("/me")
    public Mono<UserDto> updateCurrentUserProfile(@RequestBody UpdateProfileRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(username -> userService.getUserProfileByUsername(username)
                        .flatMap(userDto -> userService.updateUserProfile(userDto.getId(), request))
                );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> getUserProfileById(@PathVariable String id) {
        return userService.getUserProfileById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Flux<UserDto> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public Mono<UserDto> updateUserRoles(@PathVariable String id, @RequestBody UpdateUserRolesRequest request) {
        return userService.updateUserRoles(id, request.getRoles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/lock")
    public Mono<UserDto> lockUser(@PathVariable String id) {
        return userService.toggleUserLock(id, true);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unlock")
    public Mono<UserDto> unlockUser(@PathVariable String id) {
        return userService.toggleUserLock(id, false);
    }
}