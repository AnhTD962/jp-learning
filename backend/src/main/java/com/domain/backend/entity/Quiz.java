package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;
    private String deckId;
    private String deckName; // thêm để hiển thị tên deck
    private String userId;
    private List<QuizQuestion> questions; // Embedded documents
    private int score;
    private Instant completedAt;
    private boolean isCompleted;
}
