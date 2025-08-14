package com.domain.backend.messaging;

import com.domain.backend.config.MessageQueueConfig;
import com.domain.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final JavaMailSender mailSender;
    private final NotificationService notificationService;
    // private final QuizService quizService; // If QuizService needs to be triggered by MQ for heavy tasks

    @RabbitListener(queues = MessageQueueConfig.EMAIL_QUEUE)
    public void handleEmailNotification(EmailMessage message) {
        System.out.println("Received email message: " + message.getTo());
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(message.getTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getBody());
            mailSender.send(mailMessage);
            System.out.println("Email sent successfully to: " + message.getTo());
        } catch (Exception e) {
            System.err.println("Failed to send email to " + message.getTo() + ": " + e.getMessage());
            // Implement retry logic or dead-letter queue here
        }
    }

    @RabbitListener(queues = MessageQueueConfig.QUIZ_RESULT_QUEUE)
    public void handleQuizCompletion(QuizCompletionMessage message) {
        System.out.println("Received quiz completion message for user: " + message.getUserId());
        // Trigger notification service for real-time update
        notificationService.sendQuizCompletionNotification(message.getUserId(), message.getScore())
                .subscribe(
                        dto -> System.out.println("Quiz completion notification sent for user: " + dto.getUserId()),
                        error -> System.err.println("Failed to send quiz completion notification: " + error.getMessage())
                );
        // Additional background processing like updating long-term stats could go here
    }

    // Example for another background task
    @RabbitListener(queues = MessageQueueConfig.BACKGROUND_TASK_QUEUE)
    public void handleBackgroundTask(BackgroundTaskMessage message) {
        System.out.println("Received background task message: " + message.getTaskId());
        // Perform heavy computations or batch operations
    }
}