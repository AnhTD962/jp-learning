package com.domain.backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDto {
    private String id;

    @NotBlank(message = "Japanese word cannot be blank")
    private String japanese;

    @NotBlank(message = "Vietnamese translation cannot be blank")
    private String vietnamese;

    private String exampleSentence;
    private String exampleSentenceTranslation;

    @NotBlank(message = "Word type cannot be blank")
    private String type;        // "alphabet", "kanji", "romaji"

    private String subType;     // "hiragana", "katakana" (when type is "alphabet")

    private String kun;     // 訓読み
    private String on;      // 音読み

    // Helper methods for validation and logic
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

    // Validation helper - you might want to use this in a custom validator
    public boolean isValidTypeSubTypeCombination() {
        if ("alphabet".equals(type)) {
            return "hiragana".equals(subType) || "katakana".equals(subType);
        } else if ("kanji".equals(type) || "romaji".equals(type)) {
            return subType == null || subType.trim().isEmpty();
        }
        return false;
    }
}
