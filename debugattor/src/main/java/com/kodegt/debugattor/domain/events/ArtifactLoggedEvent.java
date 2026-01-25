package com.kodegt.debugattor.domain.events;

import java.util.UUID;

public record ArtifactLoggedEvent(UUID stepId, UUID artifactId, String type, String description, String content, String url) {
}

