package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "words")
public class Word {
    @Id
    private String id;
    private String japanese;
    private String vietnamese;
    private String exampleSentence;
    private String exampleSentenceTranslation;

    // Option 1: Two separate fields for main type and subtype
    private String type;        // "alphabet", "kanji", "romaji"
    private String subType;     // "hiragana", "katakana" (when type is "alphabet")

    private String kun;     // Japanese native reading (訓読み)
    private String on;      // Sino-Japanese reading (音読み)

    // Option 2: Single detailed type field (alternative approach)
    // private String detailedType; // "hiragana", "katakana", "kanji", "romaji"

    // Helper methods for Option 1
    public boolean isAlphabet() {
        return "alphabet".equals(type);
    }

    public boolean isHiragana() {
        return isAlphabet() && "hiragana".equals(subType);
    }

    public boolean isKatakana() {
        return isAlphabet() && "katakana".equals(subType);
    }

    public boolean isKanji() {
        return "kanji".equals(type);
    }

    public void validateKanjiFields() {
        if (!isKanji()) {
            this.kun = null;
            this.on = null;
        }
    }
}