package com.domain.backend.service;

import com.domain.backend.entity.Word;
import com.domain.backend.entity.FlashcardDeck;
import com.domain.backend.repository.WordRepository;
import com.domain.backend.repository.FlashcardDeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final WordRepository wordRepository;
    private final FlashcardDeckRepository deckRepository;

    public Mono<Map<String, Object>> globalSearch(String keyword) {
        return Mono.zip(
                wordRepository.findByJapaneseContainingIgnoreCaseOrVietnameseContainingIgnoreCase(keyword, keyword).collectList(),
                deckRepository.findByNameContainingIgnoreCase(keyword).collectList()
        ).map(tuple -> {
            Map<String, Object> result = new HashMap<>();
            result.put("words", tuple.getT1());
            result.put("decks", tuple.getT2());
            return result;
        });
    }
}
