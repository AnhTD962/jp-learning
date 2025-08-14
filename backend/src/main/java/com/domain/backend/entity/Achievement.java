package com.domain.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "achievements")
public class Achievement {
    @Id
    private String id;
    private String name;
    private String description;
    private String iconUrl; // Or reference to SVG/emoji
    private int requiredXp; // Example criteria
}