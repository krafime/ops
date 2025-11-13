package com.dansmultipro.ops.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

@Configuration
public class RabbitConfig {
    public static final String EMAIL_EX = "email.notification.exchange";
    public static final String EMAIL_QUEUE_SUCCESS = "email.notification.success.queue";
    public static final String EMAIL_RK_SUCCESS = "email.notification.success.key";
    public static final String EMAIL_QUEUE_FAILED = "email.notification.failed.queue";
    public static final String EMAIL_RK_FAILED = "email.notification.failed.key";
    public static final String EMAIL_QUEUE_FORGOT_PASSWORD = "email.notification.forgot.password.queue";
    public static final String EMAIL_RK_FORGOT_PASSWORD = "email.notification.forgot.password.key";

    // Dead Letter Exchange and Queue
    public static final String EMAIL_EX_DLQ = "email.notification.dlq.exchange";
    public static final String EMAIL_QUEUE_DLQ = "email.notification.dlq.queue";
    public static final String EMAIL_RK_DLQ = "email.notification.dlq.key";

    // Main Exchange
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EX);
    }

    // DLX (Dead Letter Exchange)
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(EMAIL_EX_DLQ, true, false);
    }

    // DLQ (Dead Letter Queue)
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE_DLQ).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(EMAIL_RK_DLQ);
    }

    // Email queues dengan DLX configuration
    @Bean
    public Queue emailQueueSuccess() {
        return QueueBuilder.durable(EMAIL_QUEUE_SUCCESS)
                .deadLetterExchange(EMAIL_EX_DLQ)
                .deadLetterRoutingKey(EMAIL_RK_DLQ)
                .build();
    }

    @Bean
    public Binding emailBindingSuccess() {
        return BindingBuilder.bind(emailQueueSuccess())
                .to(emailExchange())
                .with(EMAIL_RK_SUCCESS);
    }

    @Bean
    public Queue emailQueueFailed() {
        return QueueBuilder.durable(EMAIL_QUEUE_FAILED)
                .deadLetterExchange(EMAIL_EX_DLQ)
                .deadLetterRoutingKey(EMAIL_RK_DLQ)
                .build();
    }

    @Bean
    public Binding emailBindingFailed() {
        return BindingBuilder.bind(emailQueueFailed())
                .to(emailExchange())
                .with(EMAIL_RK_FAILED);
    }

    @Bean
    public Queue emailQueueForgotPassword() {
        return QueueBuilder.durable(EMAIL_QUEUE_FORGOT_PASSWORD)
                .deadLetterExchange(EMAIL_EX_DLQ)
                .deadLetterRoutingKey(EMAIL_RK_DLQ)
                .build();
    }

    @Bean
    public Binding emailBindingForgotPassword() {
        return BindingBuilder.bind(emailQueueForgotPassword())
                .to(emailExchange())
                .with(EMAIL_RK_FORGOT_PASSWORD);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        // IMPORTANT: Don't requeue rejected messages, let them go to DLX instead
        factory.setDefaultRequeueRejected(false);

        // Setup exponential backoff retry policy
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);      // Start with 1 second
        backOffPolicy.setMultiplier(2.0);            // Double each retry
        backOffPolicy.setMaxInterval(10000);         // Max 10 seconds

        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(4)                               // 1 initial + 3 retries
                        .backOffPolicy(backOffPolicy)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );

        return factory;
    }
}

