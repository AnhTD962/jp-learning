package com.domain.backend.service;

import com.domain.backend.dto.response.NotificationDto;
import com.domain.backend.entity.Notification;
import com.domain.backend.entity.NotificationType;
import com.domain.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Quản lý stream theo userId
    private final Map<String, Sinks.Many<ServerSentEvent<NotificationDto>>> userSinks = new ConcurrentHashMap<>();

    /**
     * Gửi thông báo đến user, đồng thời push qua SSE nếu đang kết nối.
     */
    public Mono<NotificationDto> sendNotification(String userId, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .createdAt(Instant.now())
                .isRead(false)
                .build();

        return notificationRepository.save(notification)
                .map(this::convertToDto)
                .doOnNext(dto -> emitToUserSink(userId, dto));
    }

    /**
     * Trả về stream gồm thông báo chưa đọc và cả thông báo realtime (SSE).
     */
    public Flux<ServerSentEvent<NotificationDto>> getNotificationsStream(String userId) {
        Sinks.Many<ServerSentEvent<NotificationDto>> sink = userSinks
                .computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());

        // Gửi thông báo chưa đọc trước, sau đó là stream realtime
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .filter(notification -> !notification.isRead())
                .map(this::convertToDto)
                .map(this::buildSseEvent)
                .concatWith(sink.asFlux())
                .doOnCancel(() -> userSinks.remove(userId)); // Clean sink nếu user ngắt kết nối
    }

    /**
     * Lấy tất cả thông báo của user theo thứ tự mới nhất trước.
     */
    public Flux<NotificationDto> getAllNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .map(this::convertToDto);
    }

    /**
     * Đánh dấu một thông báo là đã đọc.
     */
    public Mono<Void> markNotificationAsRead(String notificationId) {
        return notificationRepository.findById(notificationId)
                .flatMap(notification -> {
                    notification.setRead(true);
                    return notificationRepository.save(notification);
                })
                .then();
    }

    // ---------- Specialized Methods for Different Notification Types ----------

    public Mono<NotificationDto> sendStudyReminder(String userId, String message) {
        return sendNotification(userId, message, NotificationType.STUDY_REMINDER);
    }

    public Mono<NotificationDto> sendQuizCompletionNotification(String userId, int score) {
        String message = String.format("You completed a quiz with a score of %d!", score);
        return sendNotification(userId, message, NotificationType.QUIZ_COMPLETION);
    }

    public Mono<NotificationDto> sendAchievementNotification(String userId, String achievementName) {
        String message = String.format("Congratulations! You unlocked the achievement: %s!", achievementName);
        return sendNotification(userId, message, NotificationType.ACHIEVEMENT);
    }

    // ---------- Private Helpers ----------

    private void emitToUserSink(String userId, NotificationDto dto) {
        Sinks.Many<ServerSentEvent<NotificationDto>> sink = userSinks.get(userId);
        if (sink != null) {
            sink.tryEmitNext(buildSseEvent(dto));
        }
    }

    private ServerSentEvent<NotificationDto> buildSseEvent(NotificationDto dto) {
        return ServerSentEvent.<NotificationDto>builder()
                .id(dto.getId())
                .event(dto.getType().name())
                .data(dto)
                .build();
    }

    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .build();
    }
}
