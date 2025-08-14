package com.domain.backend.controller;

import com.domain.backend.dto.response.FlashcardDeckDto;
import com.domain.backend.dto.response.FlashcardDto;
import com.domain.backend.entity.User;
import com.domain.backend.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class FlashcardDeckController {

    private final FlashcardService flashcardService;

    @PostMapping
    public Mono<FlashcardDeckDto> createDeck(@Valid @RequestBody FlashcardDeckDto deckDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (UserDetails) ctx.getAuthentication().getPrincipal())
                .flatMap(userDetails -> flashcardService.createDeck(deckDto, userDetails.getUsername()));
    }

    @GetMapping("/my-decks")
    public Flux<FlashcardDeckDto> getDecksForCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (UserDetails) ctx.getAuthentication().getPrincipal())
                .flatMapMany(userDetails -> flashcardService.getDecksByUserId(userDetails.getUsername()));
    }

    @GetMapping
    public Flux<FlashcardDeckDto> getAllDecks() {
        return flashcardService.getAllDecks();
    }

    @GetMapping("/user/{username}")
    public Flux<FlashcardDeckDto> getDecksByUserId(@PathVariable String username) {
        return flashcardService.getDecksByUserId(username);
    }

    @GetMapping("/{id}")
    public Mono<FlashcardDeckDto> getDeckById(@PathVariable String id) {
        return flashcardService.getDeckById(id);
    }

    @PutMapping("/{id}")
    public Mono<FlashcardDeckDto> updateDeck(@PathVariable String id, @Valid @RequestBody FlashcardDeckDto deckDto,
                                             @AuthenticationPrincipal User user) {
        return flashcardService.updateDeck(id, deckDto, user.getUsername());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteDeck(@PathVariable String id, @AuthenticationPrincipal User user) {
        return flashcardService.deleteDeck(id, user.getUsername());
    }

    @PostMapping("/{deckId}/flashcards")
    public Mono<FlashcardDeckDto> addFlashcardToDeck(@PathVariable String deckId, @Valid @RequestBody FlashcardDto flashcardDto,
                                                     @AuthenticationPrincipal User user) {
        return flashcardService.addFlashcardToDeck(deckId, flashcardDto, user.getUsername());
    }

    @PutMapping("/{deckId}/flashcards/{flashcardId}")
    public Mono<FlashcardDeckDto> updateFlashcardInDeck(@PathVariable String deckId, @PathVariable String flashcardId,
                                                        @Valid @RequestBody FlashcardDto flashcardDto,
                                                        @AuthenticationPrincipal User user) {
        return flashcardService.updateFlashcardInDeck(deckId, flashcardId, flashcardDto, user.getUsername());
    }

    @DeleteMapping("/{deckId}/flashcards/{flashcardId}")
    public Mono<FlashcardDeckDto> removeFlashcardFromDeck(@PathVariable String deckId, @PathVariable String flashcardId,
                                                          @AuthenticationPrincipal User user) {
        return flashcardService.removeFlashcardFromDeck(deckId, flashcardId, user.getUsername());
    }
}