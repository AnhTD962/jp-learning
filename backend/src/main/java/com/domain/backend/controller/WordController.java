package com.domain.backend.controller;

import com.domain.backend.dto.response.WordDto;
import com.domain.backend.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping
    public Mono<ResponseEntity<WordDto>> createWord(@RequestBody @Valid Mono<WordDto> wordDtoMono) {
        return wordDtoMono
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No request body"
                )))
                .flatMap(wordService::createWord)
                .map(savedWord -> ResponseEntity.status(HttpStatus.CREATED).body(savedWord))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        String message = e.getMessage();
                        if (message.contains("already exists")) {
                            // Return 409 Conflict for duplicate words
                            return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                        }
                        // Return 400 Bad Request for other validation errors
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/{id}")
    public Mono<WordDto> getWordById(@PathVariable String id) {
        return wordService.getWordById(id);
    }

    @GetMapping
    public Flux<WordDto> getAllWords() {
        return wordService.getAllWords();
    }

    @GetMapping("/type/{type}")
    public Flux<WordDto> getWordsByType(@PathVariable String type) {
        return wordService.getWordsByType(type);
    }

    @GetMapping("/type/{type}/subtype/{subType}")
    public Flux<WordDto> getWordsByTypeAndSubType(@PathVariable String type, @PathVariable String subType) {
        return wordService.getWordsByTypeAndSubType(type, subType);
    }

    @GetMapping("/search")
    public Flux<WordDto> searchWords(@RequestParam String q) {
        return wordService.searchWords(q);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public Mono<WordDto> updateWord(@PathVariable String id, @Valid @RequestBody WordDto wordDto) {
        return wordService.updateWord(id, wordDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteWord(@PathVariable String id) {
        return wordService.deleteWord(id);
    }
}