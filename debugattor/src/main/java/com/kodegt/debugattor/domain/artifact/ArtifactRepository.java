package com.kodegt.debugattor.domain.artifact;

import java.util.List;
import java.util.UUID;

public interface ArtifactRepository {
    Artifact log(UUID stepId, Artifact.Type type, String description, String content);

    List<String> findImagesByExecutionId(UUID executionId);

    Artifact createWithoutUrl(UUID stepId, Artifact.Type type, String description);

    void updateContent(UUID artifactId, String url);
}
