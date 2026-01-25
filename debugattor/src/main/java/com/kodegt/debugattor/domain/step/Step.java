package com.kodegt.debugattor.domain.step;

import com.kodegt.debugattor.domain.artifact.Artifact;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Step(UUID id, String name, Status status, List<Artifact> artifacts, Instant registeredAt, Instant completedAt) {

    public static Step load(UUID id, String name, String status, List<Artifact> artifacts, OffsetDateTime registeredAt, OffsetDateTime completedAt) {
        return new Step(id, name, Status.valueOf(status), artifacts,
                registeredAt == null ? null : registeredAt.toInstant(),
                completedAt == null ? null : completedAt.toInstant());
    }

    public enum Status {
        RUNNING,
        COMPLETED,
        FAILED
    }

    public static Step newStep(String name) {
        return new Step(null, name, Status.RUNNING, null, null, null);
    }

}
