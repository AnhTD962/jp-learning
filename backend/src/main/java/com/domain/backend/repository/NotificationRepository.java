package com.domain.backend.repository;

import com.domain.backend.entity.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Flux<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}
