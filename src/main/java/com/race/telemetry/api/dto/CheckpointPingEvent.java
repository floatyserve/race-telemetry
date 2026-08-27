package com.race.telemetry.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CheckpointPingEvent(
        @NotNull UUID eventId,
        @NotNull Long runnerId,
        @NotBlank String checkpointId,
        @NotNull Instant timestamp,
        int waveNumber
) {}