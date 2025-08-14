package com.domain.backend.repository;

import com.domain.backend.entity.Quiz;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface QuizRepository extends ReactiveMongoRepository<Quiz, String> {
    Flux<Quiz> findByUserId(String userId);

    Flux<Quiz> findByUserIdAndCompletedTrueOrderByCompletedAtDesc(String userId);
}
