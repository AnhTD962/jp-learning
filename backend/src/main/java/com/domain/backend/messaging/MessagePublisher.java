package com.domain.backend.messaging;

import com.domain.backend.config.MessageQueueConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEmailNotification(EmailMessage message) {
        rabbitTemplate.convertAndSend(MessageQueueConfig.DIRECT_EXCHANGE, "emailRoutingKey", message);
        System.out.println("Published email message: " + message.getTo());
    }

    public void publishQuizCompletion(QuizCompletionMessage message) {
        rabbitTemplate.convertAndSend(MessageQueueConfig.DIRECT_EXCHANGE, "quizResultRoutingKey", message);
        System.out.println("Published quiz completion message for user: " + message.getUserId());
    }

    // Add other publish methods for background tasks if needed
}
