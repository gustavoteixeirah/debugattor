package com.teixeirah.debugattor.domain.step;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Step(UUID id, String name, Status status, Instant registeredAt, Instant completedAt) {

    public static Step load(UUID id, String name, String status, OffsetDateTime registeredAt, OffsetDateTime completedAt) {
        return new Step(id, name, Status.valueOf(status),
                registeredAt == null ? null : registeredAt.toInstant(),
                completedAt == null ? null : completedAt.toInstant());
    }

    public enum Status {
        RUNNING,
        COMPLETED,
        FAILED
    }

    public static Step newStep(String name) {
        return new Step(null, name, Status.RUNNING, null, null);
    }

}
