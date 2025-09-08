package com.domain.backend.repository;

import com.domain.backend.entity.Word;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WordRepository extends ReactiveMongoRepository<Word, String> {
    // Custom query to search by japanese or vietnamese fields
    @Query("{ '$or': [ { 'japanese': { '$regex': ?0, '$options': 'i' } }, { 'vietnamese': { '$regex': ?0, '$options': 'i' } } ] }")
    Flux<Word> findByJapaneseOrVietnameseRegex(String query);

    // Custom query to find words by type
    Flux<Word> findByType(String type);

    // Custom query to find words by type and subtype
    Flux<Word> findByTypeAndSubType(String type, String subType);

    Mono<Boolean> existsByJapanese(String japanese);

    Flux<Word> findByJapaneseContainingIgnoreCaseOrVietnameseContainingIgnoreCase(
            String japanese, String vietnamese
    );

}