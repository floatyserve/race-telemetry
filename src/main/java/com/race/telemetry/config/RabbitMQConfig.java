package com.race.telemetry.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "race.telemetry.exchange";
    public static final String DLX_NAME = "race.telemetry.dlx";

    public static final String LEADERBOARD_QUEUE = "q.race.leaderboard";
    public static final String NOTIFICATIONS_QUEUE = "q.race.notifications";
    public static final String DEAD_LETTER_QUEUE = "q.race.dlq";

    private static final int messageTTL = 2 * 60 * 1000;

    @Bean
    public TopicExchange telemetryExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead-letter-key");
    }

    @Bean
    public Queue leaderboardQueue() {
        return QueueBuilder.durable(LEADERBOARD_QUEUE)
                .deadLetterExchange(DLX_NAME)
                .deadLetterRoutingKey("dead-letter-key")
                .build();
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(NOTIFICATIONS_QUEUE)
                .ttl(messageTTL)
                .deadLetterExchange(DLX_NAME)
                .deadLetterRoutingKey("dead-letter-key")
                .build();
    }

    @Bean
    public Binding leaderboardBinding(Queue leaderboardQueue, TopicExchange telemetryExchange) {
        return BindingBuilder.bind(leaderboardQueue).to(telemetryExchange).with("race.checkpoint.*");
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, TopicExchange telemetryExchange) {
        return BindingBuilder.bind(notificationsQueue).to(telemetryExchange).with("race.checkpoint.finish");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}