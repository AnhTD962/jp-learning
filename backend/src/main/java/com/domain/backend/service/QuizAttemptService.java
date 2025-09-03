package com.domain.backend.service;

import com.domain.backend.dto.request.QuizSubmissionRequest;
import com.domain.backend.dto.response.*;
import com.domain.backend.entity.*;
import com.domain.backend.messaging.MessagePublisher;
import com.domain.backend.repository.FlashcardDeckRepository;
import com.domain.backend.repository.QuizAttemptRepository;
import com.domain.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
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
     */
    public Mono<QuizAttemptResponseDto> startAttempt(String quizId, String username) {
        log.info("➡️ [START_ATTEMPT] quizId={}, username={}", quizId, username);

        return quizAttemptRepository.findByQuizIdAndUserIdAndCompletedFalse(quizId, username)
                .flatMap(existing -> {
                    log.info("♻️ [ATTEMPT_REUSED] attemptId={}, quizId={}, deckId={}, username={}",
                            existing.getId(), existing.getQuizId(), existing.getDeckId(), existing.getUserId());
                    return Mono.just(new QuizAttemptResponseDto(
                            existing.getId(),
                            existing.getQuizId(),
                            existing.getDeckId(),
                            existing.getDeckName(),
                            existing.getQuestionsSnapshot()
                    ));
                })
                .switchIfEmpty(
                        quizRepository.findById(quizId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Quiz not found")))
                                .flatMap(quiz -> {
                                    QuizAttempt attempt = new QuizAttempt();
                                    attempt.setQuizId(quiz.getId());
                                    attempt.setDeckId(quiz.getDeckId());
                                    attempt.setDeckName(quiz.getDeckName());
                                    attempt.setUserId(username);

                                    // snapshot questions
                                    List<QuizQuestion> snapshot = Optional.ofNullable(quiz.getQuestions())
                                            .orElse(Collections.emptyList())
                                            .stream()
                                            .map(q -> {
                                                QuizQuestion copy = new QuizQuestion();
                                                copy.setQuestionId(q.getQuestionId());
                                                copy.setFlashcardId(q.getFlashcardId());
                                                copy.setQuestionText(q.getQuestionText());
                                                copy.setOptions(q.getOptions() == null ? new ArrayList<>() : new ArrayList<>(q.getOptions()));
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
                                            .doOnSuccess(saved ->
                                                    log.info("✅ [ATTEMPT_CREATED] attemptId={}, quizId={}, deckId={}, username={}",
                                                            saved.getId(), saved.getQuizId(), saved.getDeckId(), saved.getUserId())
                                            )
                                            .map(saved -> new QuizAttemptResponseDto(
                                                    saved.getId(),
                                                    saved.getQuizId(),
                                                    saved.getDeckId(),
                                                    saved.getDeckName(),
                                                    saved.getQuestionsSnapshot()
                                            ));
                                })
                );
    }

    /**
     * Submit an attempt.
     */
    public Mono<QuizAttemptResultDto> submitAttempt(String attemptId, QuizSubmissionRequest submission, String username) {
        log.info("➡️ [SUBMIT_ATTEMPT] attemptId={}, username={}", attemptId, username);

        return quizAttemptRepository.findById(attemptId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Attempt not found")))
                .flatMap(attempt -> {
                    if (!Objects.equals(attempt.getUserId(), username)) {
                        return Mono.error(new AccessDeniedException("Not allowed to submit this attempt"));
                    }
                    if (attempt.isCompleted()) {
                        return Mono.error(new IllegalStateException("Attempt already submitted"));
                    }

                    Map<String, String> submissionAnswers = submission.getAnswers();
                    if (submissionAnswers == null) {
                        submissionAnswers = new HashMap<>();
                    }
                    final Map<String, String> finalAnswers = submissionAnswers;

                    List<QuizQuestion> snapshot = attempt.getQuestionsSnapshot();
                    if (snapshot == null || snapshot.isEmpty()) {
                        return Mono.error(new IllegalStateException("Attempt has no snapshot questions"));
                    }

                    AtomicInteger score = new AtomicInteger(0);
                    List<QuizQuestionResultDto> questionResults = new ArrayList<>();

                    for (QuizQuestion q : snapshot) {
                        if (q == null || q.getQuestionId() == null) continue;

                        // ✅ Lấy theo questionId thay vì flashcardId
                        String userAnswer = finalAnswers.getOrDefault(q.getQuestionId(), "");
                        if (userAnswer != null) userAnswer = userAnswer.trim();

                        boolean correct = q.getCorrectAnswer() != null &&
                                q.getCorrectAnswer().equalsIgnoreCase(userAnswer);

                        if (correct) score.incrementAndGet();

                        questionResults.add(new QuizQuestionResultDto(
                                q.getQuestionId(),
                                q.getFlashcardId(),
                                q.getQuestionText(),
                                q.getCorrectAnswer(),
                                userAnswer,
                                correct
                        ));
                    }

                    attempt.setAnswers(new HashMap<>(finalAnswers));
                    attempt.setScore(score.get());
                    attempt.setCompletedAt(Instant.now());
                    attempt.setCompleted(true);

                    return quizAttemptRepository.save(attempt)
                            .map(savedAttempt -> new QuizAttemptResultDto(
                                    savedAttempt.getId(),
                                    savedAttempt.getQuizId(),
                                    savedAttempt.getDeckId(),
                                    savedAttempt.getDeckName(),
                                    savedAttempt.getUserId(),
                                    savedAttempt.getScore(),
                                    savedAttempt.getCompletedAt(),
                                    new ArrayList<>(questionResults)
                            ));
                });
    }

    /**
     * Get attempt result.
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
     * List attempts for a deck (only deck owner).
     */
    public Flux<QuizAttemptResultDto> listAttemptsForDeck(String deckId, String requesterUsername) {
        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Deck not found")))
                .flatMapMany(deck -> {
                    if (!Objects.equals(deck.getUserId(), requesterUsername)) {
                        return Flux.error(new AccessDeniedException("Not allowed to view attempts for this deck"));
                    }
                    return quizAttemptRepository.findByDeckId(deckId)
                            .flatMap(this::buildResultDtoFromAttempt);
                });
    }

    /**
     * List attempts for the current user.
     */
    public Flux<QuizAttemptResultDto> listAttemptsForUser(String userId) {
        return quizAttemptRepository.findByUserId(userId)
                .flatMap(this::buildResultDtoFromAttempt);
    }

    /**
     * Build result DTO from attempt (always use snapshot).
     */
    private Mono<QuizAttemptResultDto> buildResultDtoFromAttempt(QuizAttempt attempt) {
        List<QuizQuestion> snapshot = attempt.getQuestionsSnapshot();
        if (snapshot == null || snapshot.isEmpty()) {
            return Mono.error(new IllegalStateException("Attempt has no snapshot questions"));
        }

        Map<String, String> answers = attempt.getAnswers();
        if (answers == null) {
            answers = Collections.emptyMap();
        }
        final Map<String, String> finalAnswers = answers;

        List<QuizQuestionResultDto> qResults = snapshot.stream()
                .filter(q -> q != null && q.getQuestionId() != null)
                .map(q -> {
                    String userAns = finalAnswers.getOrDefault(q.getQuestionId(), "");
                    boolean correct = q.getCorrectAnswer() != null &&
                            q.getCorrectAnswer().equalsIgnoreCase(userAns);
                    return new QuizQuestionResultDto(
                            q.getQuestionId(),
                            q.getFlashcardId(),
                            q.getQuestionText(),
                            q.getCorrectAnswer(),
                            userAns,
                            correct
                    );
                })
                .collect(Collectors.toList());

        return Mono.just(new QuizAttemptResultDto(
                attempt.getId(),
                attempt.getQuizId(),
                attempt.getDeckId(),
                attempt.getDeckName(),
                attempt.getUserId(),
                attempt.getScore(),
                attempt.getCompletedAt(),
                qResults
        ));
    }

    private int calculateXpForQuiz(int score, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 0;
        }
        return score * 10;
    }
}
