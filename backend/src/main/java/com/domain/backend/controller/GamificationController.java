package com.domain.backend.controller;

import com.domain.backend.dto.response.UserDto;
import com.domain.backend.entity.Achievement;
import com.domain.backend.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/leaderboard")
    public Flux<UserDto> getLeaderboard() {
        return gamificationService.getLeaderboard();
    }

    @GetMapping("/achievements")
    public Flux<Achievement> getAllAchievements() {
        return gamificationService.getAllAchievements();
    }
}
