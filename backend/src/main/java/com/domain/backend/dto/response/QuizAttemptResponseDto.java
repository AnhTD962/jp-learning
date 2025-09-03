package com.domain.backend.dto.response;

import com.domain.backend.entity.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptResponseDto {
    private String attemptId;
    private String quizId;
    private String deckId;
    private String deckName;
    private List<QuizQuestion> questionsSnapshot; // frontend dùng để hiển thị
}
