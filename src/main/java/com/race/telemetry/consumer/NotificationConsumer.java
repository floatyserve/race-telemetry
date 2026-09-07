package com.race.telemetry.consumer;

import com.race.telemetry.api.dto.CheckpointPingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @RabbitListener
    public void handleFinishNotification(CheckpointPingEvent event) {
        log.info("[NOTIFICATION] Runner {} crossed the FINISH line at {}. Sending alert to subscribers.",
                event.runnerId(), event.timestamp());
    }
}
