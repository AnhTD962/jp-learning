package com.domain.backend.service;

import com.domain.backend.dto.response.WordDto;
import com.domain.backend.entity.Word;
import com.domain.backend.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    public Mono<WordDto> createWord(WordDto wordDto) {
        // First check if word already exists
        return checkWordExists(wordDto.getJapanese())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Word already exists: " + wordDto.getJapanese()));
                    }
                    Word word = convertToEntity(wordDto);
                    return wordRepository.save(word).map(this::convertToDto);
                });
    }

    // Method to check if a word exists by Japanese text
    public Mono<Boolean> checkWordExists(String japanese) {
        return wordRepository.existsByJapanese(japanese);
    }

    public Mono<WordDto> getWordById(String id) {
        return wordRepository.findById(id).map(this::convertToDto);
    }

    public Flux<WordDto> getAllWords() {
        return wordRepository.findAll().map(this::convertToDto);
    }

    public Flux<WordDto> searchWords(String query) {
        return wordRepository.findByJapaneseOrVietnameseRegex(query).map(this::convertToDto);
    }

    // New method to filter words by type
    public Flux<WordDto> getWordsByType(String type) {
        return wordRepository.findByType(type).map(this::convertToDto);
    }

    // New method to filter words by type and subtype
    public Flux<WordDto> getWordsByTypeAndSubType(String type, String subType) {
        return wordRepository.findByTypeAndSubType(type, subType).map(this::convertToDto);
    }

    public Mono<WordDto> updateWord(String id, WordDto wordDto) {
        return wordRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Word not found")))
                .flatMap(existingWord -> {
                    existingWord.setJapanese(wordDto.getJapanese());
                    existingWord.setVietnamese(wordDto.getVietnamese());
                    existingWord.setExampleSentence(wordDto.getExampleSentence());
                    existingWord.setExampleSentenceTranslation(wordDto.getExampleSentenceTranslation());
                    // Add the new type fields
                    existingWord.setType(wordDto.getType());
                    existingWord.setSubType(wordDto.getSubType());
                    existingWord.setKun(wordDto.getKun());
                    existingWord.setOn(wordDto.getOn());
                    return wordRepository.save(existingWord);
                })
                .map(this::convertToDto);
    }

    public Mono<Void> deleteWord(String id) {
        return wordRepository.deleteById(id);
    }

    private WordDto convertToDto(Word word) {
        WordDto dto = new WordDto();
        dto.setId(word.getId());
        dto.setJapanese(word.getJapanese());
        dto.setVietnamese(word.getVietnamese());
        dto.setExampleSentence(word.getExampleSentence());
        dto.setExampleSentenceTranslation(word.getExampleSentenceTranslation());
        // Add the new type fields
        dto.setType(word.getType());
        dto.setSubType(word.getSubType());
        dto.setKun(word.getKun());
        dto.setOn(word.getOn());
        return dto;
    }

    private Word convertToEntity(WordDto dto) {
        Word word = new Word();
        word.setId(dto.getId()); // ID might be null for new words
        word.setJapanese(dto.getJapanese());
        word.setVietnamese(dto.getVietnamese());
        word.setExampleSentence(dto.getExampleSentence());
        word.setExampleSentenceTranslation(dto.getExampleSentenceTranslation());
        // Add the new type fields
        word.setType(dto.getType());
        word.setSubType(dto.getSubType());
        word.setKun(dto.getKun());
        word.setOn(dto.getOn());
        return word;
    }
}