package com.dansmultipro.ops.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EMAIL_EX = "email.notification.exchange";
    public static final String EMAIL_QUEUE_SUCCESS = "email.notification.success.queue";
    public static final String EMAIL_RK_SUCCESS = "email.notification.success.key";
    public static final String EMAIL_QUEUE_FAILED = "email.notification.failed.queue";
    public static final String EMAIL_RK_FAILED = "email.notification.failed.key";
    public static final String EMAIL_QUEUE_FORGOT_PASSWORD = "email.notification.forgot.password.queue";
    public static final String EMAIL_RK_FORGOT_PASSWORD = "email.notification.forgot.password.key";


    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EX);
    }

    @Bean
    public Queue emailQueueSuccess() {
        return QueueBuilder.durable(EMAIL_QUEUE_SUCCESS).build();
    }

    @Bean
    public Binding emailBindingSuccess() {
        return BindingBuilder.bind(emailQueueSuccess())
                .to(emailExchange())
                .with(EMAIL_RK_SUCCESS);
    }

    @Bean
    public Queue emailQueueFailed() {
        return QueueBuilder.durable(EMAIL_QUEUE_FAILED).build();
    }

    @Bean
    public Binding emailBindingFailed() {
        return BindingBuilder.bind(emailQueueFailed())
                .to(emailExchange())
                .with(EMAIL_RK_FAILED);
    }

    @Bean
    public Queue emailQueueForgotPassword() {
        return QueueBuilder.durable(EMAIL_QUEUE_FORGOT_PASSWORD).build();
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

}
