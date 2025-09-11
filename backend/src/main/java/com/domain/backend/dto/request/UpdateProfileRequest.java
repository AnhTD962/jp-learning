package com.domain.backend.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String email;
}
