package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private String flashcardId; // ID của flashcard liên quan
    private List<String> options; // For multiple choice
    private String correctAnswer;
    private String userAnswer;
    private boolean isCorrect;
}
