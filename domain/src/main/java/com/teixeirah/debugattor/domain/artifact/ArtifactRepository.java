package com.teixeirah.debugattor.domain.artifact;

import java.util.UUID;

public interface ArtifactRepository {
    Artifact log(UUID stepId, Artifact.Type type, String description, String content);
}
