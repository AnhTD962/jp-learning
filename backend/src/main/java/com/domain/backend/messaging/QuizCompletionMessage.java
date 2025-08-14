package com.domain.backend.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizCompletionMessage implements Serializable {
    private String userId;
    private int score;
    private String quizId;
}
