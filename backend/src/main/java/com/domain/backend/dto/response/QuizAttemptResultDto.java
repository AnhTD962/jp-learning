package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptResultDto {
    private String attemptId;
    private String quizId;
    private String deckId;
    private String userId;
    private int score;
    private Instant completedAt;
    private List<QuizQuestionResultDto> questionResults;
}
