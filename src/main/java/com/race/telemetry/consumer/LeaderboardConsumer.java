package com.race.telemetry.consumer;

import com.race.telemetry.api.dto.CheckpointPingEvent;
import com.race.telemetry.config.RabbitMQConfig;
import com.race.telemetry.model.entity.CheckpointPassage;
import com.race.telemetry.repository.CheckpointPassageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderboardConsumer {

    private final CheckpointPassageRepository repository;

    @RabbitListener(queues = RabbitMQConfig.LEADERBOARD_QUEUE)
    @Transactional
    public void handleCheckpointPing(CheckpointPingEvent event) {
        log.info("[LEADERBOARD] Processing runner {} at checkpoint '{}' (Event ID: {})",
                event.runnerId(), event.checkpointId(), event.eventId());

        if (repository.existsById(event.eventId())) {
            log.warn("[LEADERBOARD] Duplicate event received, skipping: {}", event.eventId());
            return;
        }

        CheckpointPassage passage = CheckpointPassage.builder()
                .eventId(event.eventId())
                .runnerId(event.runnerId())
                .checkpointId(event.checkpointId())
                .timestamp(event.timestamp())
                .waveNumber(event.waveNumber())
                .recordedAt(Instant.now())
                .build();

        repository.save(passage);
    }
}
