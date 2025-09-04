package com.domain.backend.dto.response;

import com.domain.backend.entity.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private String id;
    private String userId;
    private String message;
    private NotificationType type;
    private Instant createdAt;

    @JsonProperty("read") // ép JSON field là "read" thay vì "isRead"
    private boolean isRead;
}
