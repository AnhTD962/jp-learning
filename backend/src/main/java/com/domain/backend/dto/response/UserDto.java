package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private int xpPoints;
    private int studyStreak;
    private List<String> achievementIds; // IDs of unlocked achievements
    private Set<String> roles; // Roles as strings
}
