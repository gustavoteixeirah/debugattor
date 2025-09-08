package com.teixeirah.debugattor.domain.artifact;

public interface ArtifactRepository {
    Artifact log(Artifact.Type type, String content);
}
