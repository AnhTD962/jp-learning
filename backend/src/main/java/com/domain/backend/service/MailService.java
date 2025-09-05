package com.domain.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public Mono<Void> sendMail(String to, String subject, String content) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        });
    }

    public Mono<Void> sendInvoiceEmail(String to, String subject, String content) {
        return sendMail(to, subject, content);
    }
}
