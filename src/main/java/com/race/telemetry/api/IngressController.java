package com.race.telemetry.api;

import com.race.telemetry.api.dto.CheckpointPingEvent;
import com.race.telemetry.producer.TelemetryProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
@Tag(
        name = "Telemetry Ingress",
        description = "High-throughput RFID mat event ingestion"
)
public class IngressController {

    private final TelemetryProducer telemetryProducer;

    @PostMapping
    @Operation(
            summary = "Ingest checkpoint ping",
            description = "Asynchronously ingests runner RFID mat crossings into the event broker"
    )
    @ApiResponse(responseCode = "202", description = "Ping accepted and queued for processing")
    public ResponseEntity<Void> ingestCheckpointPing(@Valid @RequestBody CheckpointPingEvent event) {
        telemetryProducer.publishCheckpointPing(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
