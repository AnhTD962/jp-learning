package com.domain.backend.repository;

import com.domain.backend.entity.QuizAttempt;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QuizAttemptRepository extends ReactiveMongoRepository<QuizAttempt, String> {
    Flux<QuizAttempt> findByQuizId(String quizId);
    Flux<QuizAttempt> findByDeckId(String deckId);
    Flux<QuizAttempt> findByUserId(String userId);
    Mono<QuizAttempt> findByQuizIdAndUserIdAndCompletedFalse(String quizId, String userId);
}
