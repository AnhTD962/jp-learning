package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResultDto {
    private String flashcardId;
    private String correctAnswer;
    private String userAnswer;
    private boolean isCorrect;
}
