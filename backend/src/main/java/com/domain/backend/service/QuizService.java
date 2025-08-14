package com.domain.backend.service;

import com.domain.backend.dto.request.QuizRequest;
import com.domain.backend.entity.Flashcard;
import com.domain.backend.entity.Quiz;
import com.domain.backend.entity.QuizQuestion;
import com.domain.backend.repository.FlashcardDeckRepository;
import com.domain.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;

    /**
     * Generate a quiz template from a deck.
     * The returned Quiz is a template — it does NOT contain score/completed fields.
     * creatorUserId is the user who generated the template (optional field in Quiz).
     */
    public Mono<Quiz> generateQuiz(QuizRequest request, String creatorUserId) {
        final String deckId = request.getDeckId();
        final int numberOfQuestions = request.getNumberOfQuestions();

        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Deck not found")))
                .flatMap(deck -> {
                    List<Flashcard> all = Optional.ofNullable(deck.getFlashcards()).orElse(List.of());
                    if (all.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("This deck has no flashcards."));
                    }

                    // Choose N random flashcards
                    List<Flashcard> pool = new ArrayList<>(all);
                    Collections.shuffle(pool);
                    int n = numberOfQuestions > 0 ? Math.min(numberOfQuestions, pool.size()) : pool.size();
                    List<Flashcard> picked = pool.subList(0, n);

                    List<QuizQuestion> questions = picked.stream().map(card -> {
                        String correct = card.getBack();

                        List<String> distractors = all.stream()
                                .filter(c -> !Objects.equals(c.getId(), card.getId()))
                                .map(Flashcard::getBack)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toCollection(ArrayList::new));
                        Collections.shuffle(distractors);

                        List<String> options = new ArrayList<>();
                        options.add(correct);
                        options.addAll(distractors.stream().limit(3).toList());

                        options = options.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
                        Collections.shuffle(options);
                        while (options.size() < 4) options.add("—");

                        QuizQuestion q = new QuizQuestion();
                        q.setFlashcardId(card.getId());
                        q.setOptions(options);
                        q.setCorrectAnswer(correct);
                        q.setUserAnswer(null);
                        q.setCorrect(false);
                        return q;
                    }).collect(Collectors.toList());

                    Quiz quiz = new Quiz();
                    quiz.setDeckId(deckId);
                    quiz.setQuestions(questions);
                    // optional: set creatorId/createdAt in Quiz if your entity has fields
                    // quiz.setCreatorId(creatorUserId);
                    return quizRepository.save(quiz);
                });
    }

    /**
     * Return quiz template by id.
     */
    public Mono<Quiz> getQuizById(String quizId) {
        return quizRepository.findById(quizId);
    }

    // If you want paging/listing templates, add methods here.
}
