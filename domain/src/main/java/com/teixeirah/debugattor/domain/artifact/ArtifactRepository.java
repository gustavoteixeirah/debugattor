package com.teixeirah.debugattor.domain.artifact;

public interface ArtifactRepository {
    Artifact create(Artifact.Type type, String content);
}
