package com.domain.backend.controller;

import com.domain.backend.dto.response.NotificationDto;
import com.domain.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationDto>> getNotificationsStream() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(UserDetails.class)
                .flatMapMany(userDetails -> notificationService.getNotificationsStream(userDetails.getUsername()))
                .concatWith(Flux.never()); // giữ stream mở
    }

    @GetMapping
    public Flux<NotificationDto> getAllNotifications() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(UserDetails.class)
                .flatMapMany(userDetails ->
                        notificationService.getAllNotifications(userDetails.getUsername()));
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> markNotificationAsRead(@PathVariable String id) {
        return notificationService.markNotificationAsRead(id);
    }
}
