package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {
    private String quizId;
    private int score;
    private int xpEarned;
    private List<String> newAchievements; // IDs of newly unlocked achievements
    private List<QuizQuestionResultDto> questionResults; // Details for each question
}
