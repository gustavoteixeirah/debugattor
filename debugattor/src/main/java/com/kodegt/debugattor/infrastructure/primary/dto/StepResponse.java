package com.kodegt.debugattor.infrastructure.primary.dto;

import com.kodegt.debugattor.domain.step.Step;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StepResponse(
        UUID id,
        String name,
        String status,
        Instant registeredAt,
        Instant completedAt,
        List<ArtifactResponse> artifacts
) {
    public static StepResponse from(Step step) {
        List<ArtifactResponse> artifactResponses = step.artifacts() == null
                ? List.of()
                : step.artifacts().stream().map(ArtifactResponse::from).toList();

        return new StepResponse(
                step.id(),
                step.name(),
                step.status() == null ? null : step.status().name(),
                step.registeredAt(),
                step.completedAt(),
                artifactResponses
        );
    }
}
