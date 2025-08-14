package com.domain.backend.service;

import com.domain.backend.dto.response.FlashcardDeckDto;
import com.domain.backend.dto.response.FlashcardDto;
import com.domain.backend.entity.Flashcard;
import com.domain.backend.entity.FlashcardDeck;
import com.domain.backend.repository.FlashcardDeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardDeckRepository flashcardDeckRepository;

    public Mono<FlashcardDeckDto> createDeck(FlashcardDeckDto deckDto, String userId) {
        if (deckDto.getName() == null || deckDto.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException("Deck name must not be empty"));
        }

        FlashcardDeck deck = convertToEntity(deckDto);
        deck.setUserId(userId);
        // No need to ensure mutable - convertToEntity already creates ArrayList

        return flashcardDeckRepository.save(deck).map(this::convertToDto);
    }

    public Flux<FlashcardDeckDto> getDecksByUserId(String userId) {
        return flashcardDeckRepository.findByUserId(userId).map(this::convertToDto);
    }

    public Mono<FlashcardDeckDto> getDeckById(String deckId) {
        return flashcardDeckRepository.findById(deckId)
                .map(this::convertToDto);
    }

    // FIXED: Updated updateDeck method
    public Mono<FlashcardDeckDto> updateDeck(String deckId, FlashcardDeckDto deckDto, String username) {
        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Flashcard deck not found")))
                .flatMap(deck -> {
                    if (!deck.getUserId().equals(username)) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.FORBIDDEN, "You are not the owner of this deck"));
                    }

                    deck.setName(deckDto.getName());

                    // Create completely new list - this is the safest approach
                    List<Flashcard> flashcards = new ArrayList<>();
                    if (deckDto.getFlashcards() != null) {
                        for (FlashcardDto dto : deckDto.getFlashcards()) {
                            flashcards.add(convertToEntity(dto));
                        }
                    }
                    deck.setFlashcards(flashcards);

                    return flashcardDeckRepository.save(deck);
                })
                .map(this::convertToDto);
    }

    public Mono<Void> deleteDeck(String deckId, String username) {
        return flashcardDeckRepository.findById(deckId)
                .filter(deck -> deck.getUserId().equals(username)) // chỉ xóa nếu đúng user
                .flatMap(flashcardDeckRepository::delete)
                .then(); // Kết thúc mà không báo lỗi
    }

    public Mono<FlashcardDeckDto> addFlashcardToDeck(String deckId, FlashcardDto flashcardDto, String username) {
        if (flashcardDto.getFront() == null || flashcardDto.getBack() == null) {
            return Mono.error(new IllegalArgumentException("Flashcard front and back must not be null"));
        }

        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Flashcard deck not found")))
                .flatMap(deck -> {
                    if (!deck.getUserId().equals(username)) {
                        return Mono.error(new IllegalAccessException("You are not the owner of this deck"));
                    }

                    // FIXED: Create new mutable list instead of using ensureMutableList
                    List<Flashcard> flashcards = new ArrayList<>();
                    if (deck.getFlashcards() != null) {
                        flashcards.addAll(deck.getFlashcards());
                    }

                    Flashcard flashcard = convertToEntity(flashcardDto);
                    flashcard.setId(UUID.randomUUID().toString());
                    flashcard.setMasteryLevel(0);
                    flashcard.setLastReviewed(Instant.now());

                    flashcards.add(flashcard);
                    deck.setFlashcards(flashcards);

                    return flashcardDeckRepository.save(deck).map(this::convertToDto);
                });
    }

    public Mono<FlashcardDeckDto> updateFlashcardInDeck(String deckId, String flashcardId, FlashcardDto flashcardDto, String username) {
        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Flashcard deck not found")))
                .flatMap(deck -> {
                    if (!deck.getUserId().equals(username)) {
                        return Mono.error(new IllegalAccessException("You are not the owner of this deck"));
                    }

                    // FIXED: Create new mutable list
                    List<Flashcard> flashcards = new ArrayList<>();
                    if (deck.getFlashcards() != null) {
                        flashcards.addAll(deck.getFlashcards());
                    }

                    return Mono.justOrEmpty(flashcards)
                            .flatMapMany(Flux::fromIterable)
                            .filter(fc -> fc.getId().equals(flashcardId))
                            .singleOrEmpty()
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Flashcard not found in deck")))
                            .flatMap(existingFlashcard -> {
                                existingFlashcard.setFront(flashcardDto.getFront());
                                existingFlashcard.setBack(flashcardDto.getBack());
                                existingFlashcard.setMasteryLevel(flashcardDto.getMasteryLevel());
                                existingFlashcard.setLastReviewed(Instant.now());

                                deck.setFlashcards(flashcards);
                                return flashcardDeckRepository.save(deck).map(this::convertToDto);
                            });
                });
    }

    public Mono<FlashcardDeckDto> removeFlashcardFromDeck(String deckId, String flashcardId, String username) {
        return flashcardDeckRepository.findById(deckId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Flashcard deck not found")))
                .flatMap(deck -> {
                    if (!deck.getUserId().equals(username)) {
                        return Mono.error(new IllegalAccessException("You are not the owner of this deck"));
                    }

                    // FIXED: Create new mutable list
                    List<Flashcard> flashcards = new ArrayList<>();
                    if (deck.getFlashcards() != null) {
                        flashcards.addAll(deck.getFlashcards());
                    }

                    boolean removed = flashcards.removeIf(fc -> fc.getId().equals(flashcardId));
                    if (!removed) {
                        return Mono.error(new IllegalArgumentException("Flashcard not found in deck"));
                    }

                    deck.setFlashcards(flashcards);
                    return flashcardDeckRepository.save(deck).map(this::convertToDto);
                });
    }

    public Flux<FlashcardDeckDto> getAllDecks() {
        return flashcardDeckRepository.findAll().map(this::convertToDto);
    }

//    public Flux<Flashcard> generateQuizFlashcards(String deckId, int numberOfQuestions, String username) {
//        return flashcardDeckRepository.findById(deckId)
//                .switchIfEmpty(Mono.error(new IllegalArgumentException("Flashcard deck not found")))
//                .flatMapMany(deck -> {
//                    if (!deck.getUserId().equals(username)) {
//                        return Flux.error(new IllegalAccessException("You are not the owner of this deck"));
//                    }
//
//                    // FIXED: Create new mutable list for shuffling
//                    List<Flashcard> flashcards = new ArrayList<>();
//                    if (deck.getFlashcards() != null) {
//                        flashcards.addAll(deck.getFlashcards());
//                    }
//
//                    if (flashcards.isEmpty()) {
//                        return Flux.error(new IllegalArgumentException("Deck has no flashcards to generate a quiz."));
//                    }
//
//                    Collections.shuffle(flashcards);
//                    return Flux.fromIterable(flashcards.subList(0, Math.min(numberOfQuestions, flashcards.size())));
//                });
//    }
//
//    public Flux<Flashcard> getAllFlashcardsInDeck(String deckId, String userId) {
//        return flashcardDeckRepository.findById(deckId)
//                .filter(deck -> deck.getUserId().equals(userId))
//                .flatMapMany(deck -> Flux.fromIterable(deck.getFlashcards()));
//    }

    // Helper Methods - REMOVED the problematic ensureMutableList method

    private FlashcardDeckDto convertToDto(FlashcardDeck deck) {
        List<FlashcardDto> flashcardDtos = new ArrayList<>();
        if (deck.getFlashcards() != null) {
            flashcardDtos = deck.getFlashcards()
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new FlashcardDeckDto(deck.getId(), deck.getName(), deck.getUserId(), flashcardDtos);
    }

    private FlashcardDeck convertToEntity(FlashcardDeckDto dto) {
        List<Flashcard> flashcards = new ArrayList<>();
        if (dto.getFlashcards() != null) {
            flashcards = dto.getFlashcards()
                    .stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        FlashcardDeck deck = new FlashcardDeck();
        deck.setId(dto.getId());
        deck.setName(dto.getName());
        deck.setUserId(dto.getUserId());
        deck.setFlashcards(flashcards);
        return deck;
    }

    private FlashcardDto convertToDto(Flashcard flashcard) {
        return new FlashcardDto(
                flashcard.getId(),
                flashcard.getFront(),
                flashcard.getBack(),
                flashcard.getMasteryLevel(),
                flashcard.getLastReviewed()
        );
    }

    private Flashcard convertToEntity(FlashcardDto dto) {
        Flashcard flashcard = new Flashcard();
        flashcard.setId(dto.getId());
        flashcard.setFront(dto.getFront());
        flashcard.setBack(dto.getBack());
        flashcard.setMasteryLevel(dto.getMasteryLevel());
        flashcard.setLastReviewed(dto.getLastReviewed());
        return flashcard;
    }
}