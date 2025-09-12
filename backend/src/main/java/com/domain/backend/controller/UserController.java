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

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) String search) {
        Flux<UserDto> usersFlux;
        Mono<Long> countMono;

        if (search != null && !search.isBlank()) {
            usersFlux = userService.searchUsers(search, page, size);
            countMono = userService.countSearchUsers(search);
        } else {
            usersFlux = userService.getAllUsers(page, size);
            countMono = userService.countAllUsers();
        }

        return usersFlux.collectList()
                .zipWith(countMono)
                .map(tuple -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("content", tuple.getT1());
                    response.put("totalElements", tuple.getT2());
                    return response;
                });
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