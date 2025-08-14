package com.domain.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardDto {
    private String id;
    @NotBlank(message = "Front of flashcard cannot be blank")
    private String front;
    @NotBlank(message = "Back of flashcard cannot be blank")
    private String back;
    private int masteryLevel;
    private Instant lastReviewed;
}
