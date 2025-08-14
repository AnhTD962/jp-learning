package com.domain.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardDeckDto {
    private String id;
    @NotBlank(message = "Deck name cannot be blank")
    private String name;
    private String userId; // Will be set by service based on authenticated user
    private List<FlashcardDto> flashcards; // List of flashcards in the deck
}
