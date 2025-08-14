package com.domain.backend.dto.response;

import com.domain.backend.entity.NotificationType;
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
    private boolean read;

    @Builder.Default
    private boolean isRead = false;
}
