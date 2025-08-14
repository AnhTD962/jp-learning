package com.domain.backend.controller;

import com.domain.backend.dto.request.QuizRequest;
import com.domain.backend.entity.Quiz;
import com.domain.backend.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * Generate a quiz template from a deck (optional).
     * Any logged-in user can generate a quiz template; the creatorId is set to the current user.
     */
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Quiz>> generateQuiz(@Valid @RequestBody QuizRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String username = ctx.getAuthentication().getName();
                    return quizService.generateQuiz(request, username)
                            .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Authentication required")));
    }

    /**
     * (Optional) If you need an endpoint to fetch quiz template by id
     */
    @GetMapping("/{quizId}")
    public Mono<ResponseEntity<Quiz>> getQuizTemplate(@PathVariable String quizId) {
        return quizService.getQuizById(quizId) // implement getQuizById in QuizService
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Keep other admin/management endpoints for templates here if needed (update/delete)
}
