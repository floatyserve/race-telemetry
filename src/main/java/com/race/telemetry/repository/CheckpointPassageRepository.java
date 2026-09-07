package com.race.telemetry.repository;

import com.race.telemetry.model.entity.CheckpointPassage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckpointPassageRepository extends JpaRepository<CheckpointPassage, UUID> {
    boolean existsByRunnerIdAndCheckpointId(Long runnerId, String checkpointId);
}
