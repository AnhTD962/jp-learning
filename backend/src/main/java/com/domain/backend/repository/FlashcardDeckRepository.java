package com.domain.backend.repository;

import com.domain.backend.entity.FlashcardDeck;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FlashcardDeckRepository extends ReactiveMongoRepository<FlashcardDeck, String> {

    Flux<FlashcardDeck> findByUserId(String userId);


    Flux<FlashcardDeck> findByNameContainingIgnoreCase(String name);

}
