package com.domain.backend.repository;

import com.domain.backend.entity.Achievement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AchievementRepository extends ReactiveMongoRepository<Achievement, String> {
    // No custom methods needed for now, but can add if specific queries are required
}
