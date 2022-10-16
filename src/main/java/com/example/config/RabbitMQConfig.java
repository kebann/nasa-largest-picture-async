package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PICTURES_EXCHANGE_NAME = "nasa-pictures-exchange";
    public static final String PICTURES_QUEUE_NAME = "nasa-pictures-queue";
    public static final String PICTURES_DLQ_NAME = "nasa-pictures-dlq";

    @Bean
    public Declarables bindings() {
        var queue = QueueBuilder.durable(PICTURES_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PICTURES_DLQ_NAME)
                .build();

        var dlq = QueueBuilder.durable(PICTURES_DLQ_NAME).build();
        var directExchange = new DirectExchange(PICTURES_EXCHANGE_NAME);

        return new Declarables(
                queue,
                dlq,
                directExchange,
                BindingBuilder
                        .bind(queue)
                        .to(directExchange).with("")
        );
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}