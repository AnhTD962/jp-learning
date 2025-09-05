package com.domain.backend.service;

import com.domain.backend.dto.response.UserDto;
import com.domain.backend.entity.User;
import com.domain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<User> addAchievement(String userId, String achievementId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(userRepository::save);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setXpPoints(user.getXpPoints());
        dto.setStudyStreak(user.getStudyStreak());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return dto;
    }
}