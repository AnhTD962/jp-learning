package com.domain.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {

    // Queue names
    public static final String EMAIL_QUEUE = "emailQueue";
    public static final String QUIZ_RESULT_QUEUE = "quizResultQueue";
    public static final String BACKGROUND_TASK_QUEUE = "backgroundTaskQueue";

    // Exchange name
    public static final String DIRECT_EXCHANGE = "japaneseLearningDirectExchange";

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true); // Durable queue
    }

    @Bean
    public Queue quizResultQueue() {
        return new Queue(QUIZ_RESULT_QUEUE, true);
    }

    @Bean
    public Queue backgroundTaskQueue() {
        return new Queue(BACKGROUND_TASK_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with("emailRoutingKey");
    }

    @Bean
    public Binding quizResultBinding(Queue quizResultQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(quizResultQueue).to(directExchange).with("quizResultRoutingKey");
    }

    @Bean
    public Binding backgroundTaskBinding(Queue backgroundTaskQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(backgroundTaskQueue).to(directExchange).with("backgroundTaskRoutingKey");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}