package com.domain.backend.controller;

import com.domain.backend.dto.request.QuizSubmissionRequest;
import com.domain.backend.dto.response.QuizAttemptResponseDto;
import com.domain.backend.dto.response.QuizAttemptResultDto;
import com.domain.backend.service.QuizAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    /**
     * Start an attempt for a given quiz (any logged-in user).
     * Returns attemptId so the client can submit later.
     */
    @PostMapping("/start/{quizId}")
    public Mono<ResponseEntity<QuizAttemptResponseDto>> startAttempt(@PathVariable String quizId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizAttemptService.startAttempt(quizId, username)
                            .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication required")));
    }

    /**
     * Submit an attempt (only the attempt owner can submit).
     */
    @PostMapping("/{attemptId}/submit")
    public Mono<ResponseEntity<QuizAttemptResultDto>> submitAttempt(
            @PathVariable String attemptId,
            @Valid @RequestBody QuizSubmissionRequest submission) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizAttemptService.submitAttempt(attemptId, submission, username)
                            .map(ResponseEntity::ok);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication required")));
    }

    /**
     * Get attempt result (allowed for attempt owner or deck owner).
     */
    @GetMapping("/{attemptId}/result")
    public Mono<ResponseEntity<QuizAttemptResultDto>> getAttemptResult(@PathVariable String attemptId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizAttemptService.getAttemptResult(attemptId, username)
                            .map(ResponseEntity::ok);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication required")));
    }

    /**
     * Deck owner: list all attempts for a deck.
     */
    @GetMapping("/deck/{deckId}")
    public Flux<QuizAttemptResultDto> listAttemptsForDeck(@PathVariable String deckId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMapMany(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizAttemptService.listAttemptsForDeck(deckId, username);
                });
    }

    /**
     * Current user: list own attempts.
     */
    @GetMapping("/me")
    public Flux<QuizAttemptResultDto> listMyAttempts() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMapMany(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizAttemptService.listAttemptsForUser(username);
                });
    }
}
