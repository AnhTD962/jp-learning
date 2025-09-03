package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private String questionId = UUID.randomUUID().toString(); // unique cho mỗi câu hỏi
    private String flashcardId;   // flashcard gốc
    private String questionText;  // text câu hỏi (front hoặc back)
    private List<String> options; // multiple choice
    private String correctAnswer;
    private String userAnswer;
    private boolean isCorrect;
}
