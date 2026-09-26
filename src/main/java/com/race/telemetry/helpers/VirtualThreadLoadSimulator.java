package com.race.telemetry.helpers;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class VirtualThreadLoadSimulator {

    private static final String API_URL = "http://localhost:8080/api/telemetry";
    private static final int TOTAL_REQUESTS = 10000;

    public static void main(String[] args) {
        log.info("Starting load test with " + TOTAL_REQUESTS + " virtual threads...");
        long startTime = System.currentTimeMillis();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        try (HttpClient client = HttpClient.newHttpClient();
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                executor.submit(() -> {
                    try {
                        String payload = generatePayload();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(payload))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 202) {
                            successCount.incrementAndGet();
                        } else {
                            // Log the exact HTTP error on the first failure
                            if (failCount.getAndIncrement() == 0) {
                                System.err.println("First failure - HTTP " + response.statusCode() + ": " + response.body());
                            }
                        }
                    } catch (Exception e) {
                        // Log the exact network exception on the first failure
                        if (failCount.getAndIncrement() == 0) {
                            System.err.println("First failure - Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        }
                    }
                });
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.printf("Load test completed in %d ms.\n", duration);
        System.out.printf("Successful requests: %d\n", successCount.get());
        System.out.printf("Failed requests: %d\n", failCount.get());
    }

    private static String generatePayload() {
        long runnerId = ThreadLocalRandom.current().nextLong(1, 20001);
        int wave = ThreadLocalRandom.current().nextInt(1, 4);
        String[] checkpoints = {"start", "10k", "half", "finish"};
        String checkpointId = checkpoints[ThreadLocalRandom.current().nextInt(checkpoints.length)];

        return String.format(
                """
                        {
                          "eventId": "%s",
                          "runnerId": %d,
                          "checkpointId": "%s",
                          "timestamp": "%s",
                          "waveNumber": %d
                        }
                        """,
                UUID.randomUUID(), runnerId, checkpointId, Instant.now(), wave
        );
    }
}