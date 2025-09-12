package com.domain.backend.service;

import com.domain.backend.dto.request.UpdateProfileRequest;
import com.domain.backend.dto.response.UserDto;
import com.domain.backend.entity.Role;
import com.domain.backend.entity.User;
import com.domain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<UserDto> getUserProfileByUsername(String username) {
        return userRepository.findByUsername(username)   // cần có trong UserRepository
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .map(this::convertToDto);
    }

    public Mono<UserDto> getUserProfileById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .map(this::convertToDto);
    }

    public Mono<UserDto> updateUserProfile(String userId, UpdateProfileRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    if (request.getUsername() != null && !request.getUsername().isBlank()) {
                        user.setUsername(request.getUsername());
                    }
                    if (request.getEmail() != null && !request.getEmail().isBlank()) {
                        user.setEmail(request.getEmail());
                    }
                    return userRepository.save(user);
                })
                .map(this::convertToDto);
    }

    public Mono<User> updateUserXp(String userId, int xpAmount) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    user.setXpPoints(user.getXpPoints() + xpAmount);
                    return userRepository.save(user);
                });
    }

    public Mono<User> updateStudyStreak(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    user.setStudyStreak(user.getStudyStreak() + 1); // Simple increment for now
                    return userRepository.save(user);
                });
    }

    public Mono<User> addAchievement(String username, String achievementId) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(userRepository::save);
    }

    public Flux<UserDto> getAllUsers(int page, int size) {
        return userRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(this::convertToDto);
    }

    public Flux<UserDto> searchUsers(String keyword, int page, int size) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword)
                .skip((long) page * size)
                .take(size)
                .map(this::convertToDto);
    }

    public Mono<Long> countSearchUsers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword)
                .count();
    }

    public Mono<Long> countAllUsers() {
        return userRepository.count();
    }

    public Mono<UserDto> updateUserRoles(String userId, Set<Role> roles) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    user.setRoles(roles);
                    return userRepository.save(user);
                })
                .map(this::convertToDto);
    }

    public Mono<UserDto> toggleUserLock(String userId, boolean locked) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    // bạn cần thêm field `boolean locked` trong entity User
                    user.setAccountNonLocked(!locked);
                    return userRepository.save(user);
                })
                .map(this::convertToDto);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setXpPoints(user.getXpPoints());
        dto.setStudyStreak(user.getStudyStreak());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        dto.setLocked(!user.isAccountNonLocked());
        return dto;
    }
}