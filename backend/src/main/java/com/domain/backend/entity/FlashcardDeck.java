package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flashcardDecks")
public class FlashcardDeck {
    @Id
    private String id;
    private String name;
    private String userId;
    private List<Flashcard> flashcards; // Embedded documents
}
