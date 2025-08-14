package com.domain.backend.service;

import com.domain.backend.dto.request.QuizSubmissionRequest;
import com.domain.backend.dto.response.*;
import com.domain.backend.entity.*;
import com.domain.backend.messaging.MessagePublisher;
import com.domain.backend.messaging.QuizCompletionMessage;
import com.domain.backend.repository.FlashcardDeckRepository;
import com.domain.backend.repository.QuizAttemptRepository;
import com.domain.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;
    private final MessagePublisher messagePublisher;
    private final GamificationService gamificationService;

    /**
     * Start an attempt for a quiz (snapshot questions at start).
     * Returns attempt metadata + questionsSnapshot for frontend to render.
     */
    public Mono<QuizAttemptResponseDto> startAttempt(String quizId, String username) {
        return quizRepository.findById(quizId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quiz not found")))
                .flatMap(quiz -> {
                    QuizAttempt attempt = new QuizAttempt();
                    attempt.setQuizId(quiz.getId());
                    attempt.setDeckId(quiz.getDeckId());
                    attempt.setUserId(username);

                    // snapshot questions
                    List<QuizQuestion> snapshot = Optional.ofNullable(quiz.getQuestions())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(q -> {
                                QuizQuestion copy = new QuizQuestion();
                                copy.setFlashcardId(q.getFlashcardId());
                                copy.setOptions(q.getOptions() == null ? null : List.copyOf(q.getOptions()));
                                copy.setCorrectAnswer(q.getCorrectAnswer());
                                copy.setUserAnswer(null);
                                copy.setCorrect(false);
                                return copy;
                            })
                            .collect(Collectors.toList());

                    attempt.setQuestionsSnapshot(snapshot);
                    attempt.setAnswers(new HashMap<>());
                    attempt.setCompleted(false);

                    return quizAttemptRepository.save(attempt)
                            .map(saved -> new QuizAttemptResponseDto(saved.getId(), saved.getQuizId(), saved.getDeckId(), saved.getQuestionsSnapshot()));
                });
    }

    /**
     * Submit an attempt (only attempt owner can submit).
     * Grading uses snapshot if available, otherwise uses quiz template.
     */
    public Mono<QuizAttemptResultDto> submitAttempt(String attemptId, QuizSubmissionRequest submission, String username) {
        return quizAttemptRepository.findById(attemptId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attempt not found")))
                .flatMap(attempt -> {
                    if (!Objects.equals(attempt.getUserId(), username)) {
                        return Mono.error(new AccessDeniedException("Not allowed to submit this attempt"));
                    }
                    if (attempt.isCompleted()) {
                        return Mono.error(new IllegalStateException("Attempt already submitted"));
                    }

                    // grade using snapshot if exists; if not, fallback to quiz template
                    Mono<List<QuizQuestion>> questionSourceMono = Mono.justOrEmpty(attempt.getQuestionsSnapshot())
                            .switchIfEmpty(
                                    quizRepository.findById(attempt.getQuizId())
                                            .map(q -> Optional.ofNullable(q.getQuestions()).orElse(Collections.emptyList()))
                            );

                    return questionSourceMono.flatMap(questionSource -> {
                        AtomicInteger score = new AtomicInteger(0);
                        List<QuizQuestionResultDto> questionResults = new ArrayList<>();

                        for (QuizQuestion q : Optional.ofNullable(questionSource).orElse(Collections.emptyList())) {
                            String userAnswer = submission.getAnswers().getOrDefault(q.getFlashcardId(), "").trim();
                            boolean correct = q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase(userAnswer);
                            if (correct) score.incrementAndGet();

                            questionResults.add(new QuizQuestionResultDto(
                                    q.getFlashcardId(),
                                    q.getCorrectAnswer(),
                                    userAnswer,
                                    correct
                            ));
                        }

                        attempt.setAnswers(submission.getAnswers());
                        attempt.setScore(score.get());
                        attempt.setCompletedAt(Instant.now());
                        attempt.setCompleted(true);

                        return quizAttemptRepository.save(attempt)
                                .flatMap(savedAttempt -> {
                                    int xp = calculateXpForQuiz(savedAttempt.getScore(), questionSource == null ? 0 : questionSource.size());
                                    return gamificationService.awardXp(username, xp)
                                            .flatMap(u -> gamificationService.checkAchievements(u.getId()))
                                            .map(achievements -> {
                                                messagePublisher.publishQuizCompletion(new QuizCompletionMessage(username, savedAttempt.getScore(), savedAttempt.getQuizId()));
                                                return new QuizAttemptResultDto(
                                                        savedAttempt.getId(),
                                                        savedAttempt.getQuizId(),
                                                        savedAttempt.getDeckId(),
                                                        savedAttempt.getUserId(),
                                                        savedAttempt.getScore(),
                                                        savedAttempt.getCompletedAt(),
                                                        questionResults
                                                );
                                            });
                                });
                    });
                });
    }

    /**
     * Get attempt result.
     * - Allowed if requester == attempt.userId
     * - Or allowed if requester is deck owner
     * - Otherwise denied
     */
    public Mono<QuizAttemptResultDto> getAttemptResult(String attemptId, String requesterUsername) {
        return quizAttemptRepository.findById(attemptId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attempt not found")))
                .flatMap(attempt -> {
                    if (Objects.equals(attempt.getUserId(), requesterUsername)) {
                        return buildResultDtoFromAttempt(attempt);
                    }
                    return flashcardDeckRepository.findById(attempt.getDeckId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Deck not found")))
                            .flatMap(deck -> {
                                if (Objects.equals(deck.getUserId(), requesterUsername)) {
                                    return buildResultDtoFromAttempt(attempt);
                                } else {
                                    return Mono.error(new AccessDeniedException("Not allowed to view this attempt"));
                                }
                            });
                });
    }

    /**
     * Deck owner: list attempts for a deck.
     */
    public Flux<QuizAttemptResultDto> listAttemptsForDeck(String deckId, String requesterUsername) {
        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Deck not found")))
                .flatMapMany(deck -> {
                    if (!Objects.equals(deck.getUserId(), requesterUsername)) {
                        return Flux.error(new AccessDeniedException("Not allowed to view attempts for this deck"));
                    }
                    return quizAttemptRepository.findByDeckId(deckId).flatMap(this::buildResultDtoFromAttempt);
                });
    }

    /**
     * List attempts for the current user.
     */
    public Flux<QuizAttemptResultDto> listAttemptsForUser(String userId) {
        return quizAttemptRepository.findByUserId(userId).flatMap(this::buildResultDtoFromAttempt);
    }

    /**
     * Build result DTO from attempt. Prefer snapshot; otherwise fallback to quiz template.
     */
    private Mono<QuizAttemptResultDto> buildResultDtoFromAttempt(QuizAttempt attempt) {
        // prefer snapshot for correct answers
        List<QuizQuestion> snapshot = attempt.getQuestionsSnapshot();

        if (snapshot != null && !snapshot.isEmpty()) {
            Map<String, String> answers = Optional.ofNullable(attempt.getAnswers()).orElse(Collections.emptyMap());
            List<QuizQuestionResultDto> qResults = snapshot.stream()
                    .map(q -> {
                        String userAns = answers.getOrDefault(q.getFlashcardId(), "");
                        boolean correct = q.getCorrectAnswer() != null && q.getCorrectAnswer().equalsIgnoreCase(userAns);
                        return new QuizQuestionResultDto(q.getFlashcardId(), q.getCorrectAnswer(), userAns, correct);
                    })
                    .collect(Collectors.toList());

            return Mono.just(new QuizAttemptResultDto(
                    attempt.getId(),
                    attempt.getQuizId(),
                    attempt.getDeckId(),
                    attempt.getUserId(),
                    attempt.getScore(),
                    attempt.getCompletedAt(),
                    qResults
            ));
        }

        // fallback: load quiz template
        return quizRepository.findById(attempt.getQuizId())
                .defaultIfEmpty(new Quiz())
                .map(quiz -> {
                    Map<String, String> answers = Optional.ofNullable(attempt.getAnswers()).orElse(Collections.emptyMap());
                    Map<String, String> correctMap = Optional.ofNullable(quiz.getQuestions())
                            .orElse(Collections.emptyList())
                            .stream()
                            .collect(Collectors.toMap(QuizQuestion::getFlashcardId, QuizQuestion::getCorrectAnswer, (a,b)->a));

                    List<QuizQuestionResultDto> qResults = answers.entrySet().stream()
                            .map(e -> {
                                String fid = e.getKey();
                                String userAns = e.getValue();
                                String correct = correctMap.get(fid);
                                boolean correctFlag = correct != null && correct.equalsIgnoreCase(userAns);
                                return new QuizQuestionResultDto(fid, correct, userAns, correctFlag);
                            })
                            .collect(Collectors.toList());

                    return new QuizAttemptResultDto(
                            attempt.getId(),
                            attempt.getQuizId(),
                            attempt.getDeckId(),
                            attempt.getUserId(),
                            attempt.getScore(),
                            attempt.getCompletedAt(),
                            qResults
                    );
                });
    }

    private int calculateXpForQuiz(int score, int totalQuestions) {
        return totalQuestions == 0 ? 0 : score * 10;
    }
}
