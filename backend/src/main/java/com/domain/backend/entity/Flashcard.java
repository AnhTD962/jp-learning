package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard {
    private String id; // Unique ID for flashcard within a deck
    private String front;
    private String back;
    private int masteryLevel; // 0-5
    private Instant lastReviewed;
}
