package com.race.telemetry.producer;

import com.race.telemetry.api.dto.CheckpointPingEvent;
import com.race.telemetry.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishCheckpointPing(CheckpointPingEvent event) {
        String routingKey = "race.checkpoint." + event.checkpointId().toLowerCase().trim();

        log.debug("Publishing event {} to the routing key: {}", event.eventId(), routingKey);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                routingKey,
                event
        );
    }
}
