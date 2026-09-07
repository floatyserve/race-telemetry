package com.race.telemetry.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checkpoint_passages", indexes = {
        @Index(name = "idx_runner_checkpoint", columnList = "runnerId, checkpointId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckpointPassage {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private Long runnerId;

    @Column(nullable = false)
    private String checkpointId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private int waveNumber;

    @Column(nullable = false)
    private Instant recordedAt;
}