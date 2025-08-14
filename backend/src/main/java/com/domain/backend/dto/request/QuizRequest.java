package com.domain.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {
    @NotBlank(message = "Deck ID cannot be blank")
    private String deckId;
    @Min(value = 1, message = "Number of questions must be at least 1")
    private int numberOfQuestions;
}
