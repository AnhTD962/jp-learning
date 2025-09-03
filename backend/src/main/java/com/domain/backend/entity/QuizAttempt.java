package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quizAttempts")
public class QuizAttempt {
    @Id
    private String id;
    private String quizId;   // tham chiếu tới Quiz template
    private String deckId;   // để query theo deck
    private String deckName;
    private String userId;   // ai làm attempt này

    // snapshot questions at startAttempt time (so grading is stable)
    private List<QuizQuestion> questionsSnapshot;

    private Map<String, String> answers; // flashcardId -> selected option
    private int score;
    private Instant completedAt;
    private boolean completed;
}
