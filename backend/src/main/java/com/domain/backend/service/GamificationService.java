package com.domain.backend.service;

import com.domain.backend.dto.response.UserDto;
import com.domain.backend.entity.Achievement;
import com.domain.backend.entity.User;
import com.domain.backend.repository.AchievementRepository;
import com.domain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final NotificationService notificationService;

    public Mono<User> awardXp(String username, int xpAmount) {
        log.debug("Awarding {} XP to user: {}", xpAmount, username);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found for XP award: " + username)))
                .flatMap(user -> {
                    user.setXpPoints(user.getXpPoints() + xpAmount);
                    return userRepository.save(user);
                })
                .flatMap(updatedUser -> checkAchievements(updatedUser.getUsername()).thenReturn(updatedUser)); // FIX: Use username, not ID
    }

    public Flux<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public Mono<User> updateStreak(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found for streak update: " + username)))
                .flatMap(user -> {
                    user.setStudyStreak(user.getStudyStreak() + 1);
                    return userRepository.save(user);
                });
    }

    public Mono<List> checkAchievements(String username) {
        log.debug("Checking achievements for user: {}", username);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found for achievement check: " + username)))
                .flatMap(user -> achievementRepository.findAll().collectList()
                        .flatMap(allAchievements -> {
                            Set<String> unlockedAchievements = new HashSet<>(
                                    Optional.ofNullable(user.getAchievementIds()).orElse(Collections.emptyList())
                            );

                            List<String> newlyUnlocked = new ArrayList<>();

                            for (Achievement achievement : allAchievements) {
                                if (!unlockedAchievements.contains(achievement.getId())) {
                                    boolean unlocked = false;

                                    // Example criteria
                                    if (user.getXpPoints() >= achievement.getRequiredXp()) {
                                        unlocked = true;
                                    }

                                    // TODO: Add other criteria like streak, quizzes etc.

                                    if (unlocked) {
                                        newlyUnlocked.add(achievement.getId());
                                        unlockedAchievements.add(achievement.getId());

                                        // Trigger notification async (fire-and-forget)
                                        notificationService.sendAchievementNotification(user.getId(), achievement.getName())
                                                .doOnError(error -> log.warn("Failed to send achievement notification", error))
                                                .onErrorComplete()
                                                .subscribe();
                                    }
                                }
                            }

                            if (!newlyUnlocked.isEmpty()) {
                                user.setAchievementIds(new ArrayList<>(unlockedAchievements));
                                return userRepository.save(user).thenReturn(newlyUnlocked);
                            } else {
                                return Mono.just(Collections.emptyList());
                            }
                        }))
                .doOnError(error -> log.error("Error checking achievements for user {}", username, error))
                .cast(List.class); // Fix type inference
    }

    public Flux<UserDto> getLeaderboard() {
        return userRepository.findAll()
                .sort((u1, u2) -> Integer.compare(u2.getXpPoints(), u1.getXpPoints()))
                .take(10)
                .map(this::convertToDto);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setXpPoints(user.getXpPoints());
        dto.setStudyStreak(user.getStudyStreak());
        dto.setAchievementIds(Optional.ofNullable(user.getAchievementIds()).orElse(new ArrayList<>()));
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return dto;
    }
}