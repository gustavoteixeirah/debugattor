package com.kodegt.debugattor.infrastructure.primary.dto;

import com.kodegt.debugattor.domain.artifact.Artifact;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ArtifactResponse(
        UUID id,
        String type,
        String description,
        String content,
        OffsetDateTime loggedAt
) {
    public static ArtifactResponse from(Artifact artifact) {
        return new ArtifactResponse(
                artifact.id(),
                artifact.type() == null ? null : artifact.type().name(),
                artifact.description(),
                artifact.content(),
                artifact.loggedAt()
        );
    }
}
