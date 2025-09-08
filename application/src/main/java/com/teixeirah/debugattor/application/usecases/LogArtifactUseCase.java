package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.artifact.Artifact;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class LogArtifactUseCase {

    private final ArtifactRepository artifactRepository;

    public Artifact log(UUID stepId, Artifact.Type type, String content) {
        return artifactRepository.log(stepId, type, content);
    }

}
