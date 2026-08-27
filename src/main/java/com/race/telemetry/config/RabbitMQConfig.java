package com.race.telemetry.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "race.telemetry.exchange";
    public static final String LEADERBOARD_QUEUE = "q.race.leaderboard";
    public static final String NOTIFICATIONS_QUEUE = "q.race.notifications";

    private static final int messageTTL = 2 * 60 * 1000;

    @Bean
    public TopicExchange telemetryExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue leaderboardQueue() {
        return QueueBuilder.durable(LEADERBOARD_QUEUE).build();
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(NOTIFICATIONS_QUEUE)
                .ttl(messageTTL)
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